package com.migration.zilliah.services;

import com.migration.zilliah.APIClient.ShopifyAPIClient;
import com.migration.zilliah.APIClient.WooCommerceAPIClient;
import com.migration.zilliah.shopifymodels.Product.ShopifyProduct;
import com.migration.zilliah.shopifymodels.Product.ShopifyProductImage;
import com.migration.zilliah.shopifymodels.Product.ShopifyProductOptions;
import com.migration.zilliah.shopifymodels.Product.ShopifyProductVariant;
import com.migration.zilliah.woocommercemodels.Products.*;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ProductMigrationService {
    private final ShopifyAPIClient shopifyApiClient;
    private final WooCommerceAPIClient wooCommerceApiClient;
    private final Logger logger = LoggerFactory.getLogger(ProductMigrationService.class);


    @Autowired
   public ProductMigrationService(ShopifyAPIClient shopifyApiClient, WooCommerceAPIClient wooCommerceApiClient) {
        this.shopifyApiClient = shopifyApiClient;
        this.wooCommerceApiClient = wooCommerceApiClient;
    }

    public void migrateProduct() {
        logger.info("Starting product migration...");

        Set<String> migratedProductSkus = new HashSet<>();
        // Fetch the SKUs of products that have already been migrated
        List<WooCommerceProduct> migratedProducts = wooCommerceApiClient.getAllWooCommerceProducts();
        for (WooCommerceProduct migratedProduct : migratedProducts) {
            migratedProductSkus.add(migratedProduct.getSku());
        }

        try {
            List<ShopifyProduct> shopifyProducts = shopifyApiClient.getShopifyProducts();

            for (ShopifyProduct shopifyProduct : shopifyProducts) {
                String productSku = extractSkuFromVariants(shopifyProduct.getVariants());

                if (migratedProductSkus.contains(productSku)) {
                    logger.info("Skipping product (already migrated): {}", shopifyProduct.getTitle());
                    continue; // Skip this product
                }

                logger.info("Migrating product: {}", shopifyProduct.getTitle());
                try {
                    WooCommerceProduct wooCommerceProduct = mapToWooCommerceProduct(shopifyProduct);
                    wooCommerceApiClient.createWooCommerceProduct(wooCommerceProduct);
                    logger.info("Product migration completed: {}", wooCommerceProduct.getName());
                } catch (Exception e) {
                    logger.error("Error migrating product: {}", shopifyProduct.getTitle(), e);
                }
            }

            logger.info("Product migration completed.");
        } catch (Exception e) {
            logger.error("Error fetching Shopify products or during migration process.", e);
        }
    }


    private WooCommerceProduct mapToWooCommerceProduct(ShopifyProduct shopifyProduct) {
        WooCommerceProduct wooCommerceProduct = new WooCommerceProduct();
//        wooCommerceProduct.setId(shopifyProduct.getId());
        wooCommerceProduct.setName(shopifyProduct.getTitle());
        wooCommerceProduct.setSlug(shopifyProduct.getHandle());
        wooCommerceProduct.setDateCreated(shopifyProduct.getCreatedAt().toLocalDateTime());
        wooCommerceProduct.setDateModified(shopifyProduct.getPublishedAt().toLocalDateTime());

        createSentences(shopifyProduct.getBodyHtml(), wooCommerceProduct); // Extract sentences and set short description and description

        // Extract and set SKU
        String sku = extractSkuFromVariants(shopifyProduct.getVariants());
        wooCommerceProduct.setSku(sku);

        // Extract and set Price
        String price = extractPriceFromVariants(shopifyProduct.getVariants());
        wooCommerceProduct.setPrice(price);
        wooCommerceProduct.setRegularPrice(price);

        // Extract and set tags
        List<WooCommerceProductTag> tags = extractTagsFromShopify(shopifyProduct.getTags());
        wooCommerceProduct.setTags(tags);

        // Convert weight from double to string and set
        String weight = convertWeightToString(
                shopifyProduct.getVariants().get(0).getWeight() ,
                shopifyProduct.getVariants().get(0).getWeightUnit()
        );
        wooCommerceProduct.setWeight(weight);

        // Extract inventory_quantity and set stock_quantity
        int inventoryQuantity = extractInventoryQuantityFromVariants(shopifyProduct.getVariants());
        wooCommerceProduct.setStockQuantity(inventoryQuantity);

        // Convert taxable to tax status and set
        boolean taxable = shopifyProduct.getVariants().get(0).isTaxable();
        String taxStatus = convertTaxableToTaxStatus(taxable);
        wooCommerceProduct.setTaxStatus(taxStatus);

        List<WooCommerceProductCategory> categories = mapCategories(shopifyProduct);
        wooCommerceProduct.setCategories(categories);

        List<WooCommerceProductImage> woocommerceImages = mapImages(shopifyProduct.getImages());
        wooCommerceProduct.setImages(woocommerceImages);

        List<WooCommerceProductAttribute> attributes = mapAttributes(shopifyProduct.getOptions());
        wooCommerceProduct.setAttributes(attributes);

        String wooCommerceStockStatus = convertShopifyStatusToWooCommerceStockStatus(shopifyProduct.getStatus());
        wooCommerceProduct.setStockStatus(wooCommerceStockStatus);

        // Apply additional logic for stock status
        if (inventoryQuantity == 0) {
            wooCommerceProduct.setStockStatus("outofstock");
        }

        return wooCommerceProduct;
    }

    private String convertShopifyStatusToWooCommerceStockStatus(String shopifyStatus) {
        return switch (shopifyStatus) {
            case "active" -> "instock";
            case "archived" -> "outofstock";
            case "draft" -> "outofstock"; // Draft products can be considered as out of stock
            default -> "instock"; // Default to in stock if status is not recognized
        };
    }

    private List<WooCommerceProductAttribute> mapAttributes(List<ShopifyProductOptions> shopifyOptions) {
        List<WooCommerceProductAttribute> attributes = new ArrayList<>();

        for (ShopifyProductOptions shopifyOption : shopifyOptions) {
            WooCommerceProductAttribute attribute = new WooCommerceProductAttribute();
//            attribute.setId((int) shopifyOption.getId());
            attribute.setName(shopifyOption.getName());
            attribute.setPosition(shopifyOption.getPosition());

            List<String> options = new ArrayList<>(shopifyOption.getValues());
            attribute.setOptions(options);

            attributes.add(attribute);
        }

        return attributes;
    }

    private List<WooCommerceProductImage> mapImages(List<ShopifyProductImage> shopifyImages) {
        List<WooCommerceProductImage> woocommerceImages = new ArrayList<>();

        for (ShopifyProductImage shopifyImage : shopifyImages) {
            WooCommerceProductImage woocommerceImage = new WooCommerceProductImage();
//            woocommerceImage.setId((int) shopifyImage.getId());
            woocommerceImage.setSrc(shopifyImage.getSrc());
            woocommerceImages.add(woocommerceImage);
        }

        return woocommerceImages;
    }


    private List<WooCommerceProductCategory> mapCategories(ShopifyProduct shopifyProduct) {
        List<WooCommerceProductCategory> categories = new ArrayList<>();

        // Map Shopify vendor to WooCommerce category name
        if (shopifyProduct.getVendor() != null && !shopifyProduct.getVendor().isEmpty()) {
            WooCommerceProductCategory vendorCategory = new WooCommerceProductCategory();
            vendorCategory.setName(shopifyProduct.getVendor());
            categories.add(vendorCategory);
        }

        // Map Shopify product_type to WooCommerce category slug
        String productType = shopifyProduct.getProductType();
        if (productType != null && !productType.isEmpty()) {

            if (productType.contains("&")) {
                productType = productType.replace("&", "and");
            }

            WooCommerceProductCategory productTypeCategory = new WooCommerceProductCategory();
            productTypeCategory.setSlug(productType.toLowerCase().replace(" ", "-"));
            categories.add(productTypeCategory);
        }

        return categories;
    }

    private String convertTaxableToTaxStatus(boolean taxable) {
        return taxable ? "taxable" : "none";
    }

    private String convertWeightToString(double weight, String weightUnit) {
        return String.format("%.2f %s", weight, weightUnit);
    }

    private List<WooCommerceProductTag> extractTagsFromShopify(String tagsStr) {
        List<WooCommerceProductTag> tags = new ArrayList<>();
        if (tagsStr != null && !tagsStr.isEmpty()) {
            String[] tagNames = tagsStr.split(",");
            for (String tagName : tagNames) {
                tagName = tagName.trim();
                if (!tagName.isEmpty()) {
                    WooCommerceProductTag tag = new WooCommerceProductTag();
                    tag.setName(tagName);
                    tags.add(tag);
                }
            }
        }
        return tags;
    }

    private int extractInventoryQuantityFromVariants(List<ShopifyProductVariant> variants) {
        if (variants != null && !variants.isEmpty()) {
            ShopifyProductVariant firstVariant = variants.get(0); // Assuming you want to extract from the first variant
            return firstVariant.getInventoryQuantity();
        }
        return 0; // Default value if no variants or inventory_quantity available
    }

    private String extractPriceFromVariants(List<ShopifyProductVariant> variants) {
        if (variants != null && !variants.isEmpty()) {
            ShopifyProductVariant firstVariant = variants.get(0); // Assuming you want to extract from the first variant
            return firstVariant.getPrice();
        }
        return null;
    }

    private String extractSkuFromVariants(List<ShopifyProductVariant> variants) {
        if (variants != null && !variants.isEmpty()) {
            ShopifyProductVariant firstVariant = variants.get(0); // Assuming you want to extract from the first variant
            return firstVariant.getSku();
        }
        return null;
    }

    private void createSentences(String htmlContent, WooCommerceProduct wooCommerceProduct) {
        List<String> extractedSentences = extractSentencesFromHtml(htmlContent);

        // Set the first sentence as the short description
        if (!extractedSentences.isEmpty()) {
            wooCommerceProduct.setShortDescription(extractedSentences.get(0));
        }

        // Join remaining sentences for description
        if (extractedSentences.size() > 1) {
            String description = String.join(" ", extractedSentences.subList(1, extractedSentences.size()));
            wooCommerceProduct.setDescription(description);
        }
    }

    // Helper Method to extract sentences from HTML content
    public List<String> extractSentencesFromHtml(String htmlContent) {
        // Load the sentence model
        try (InputStream modelIn = getClass().getResourceAsStream("/en-sent.bin")) {
            assert modelIn != null;
            SentenceModel model = new SentenceModel(modelIn);
            return getStrings(htmlContent, model);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>(); // Return an empty list on error
        }
    }

    @NotNull
    private static List<String> getStrings(String htmlContent, SentenceModel model) {
        SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);

        String[] sentences = sentenceDetector.sentDetect(htmlContent);

        // Convert the array to List and trim white-space
        List<String> cleanSentences = new ArrayList<>();
        for (String sentence : sentences) {
            String trimmedSentence = sentence.trim();
            if (!trimmedSentence.isEmpty()) {
                cleanSentences.add(trimmedSentence);
            }
        }
        return cleanSentences;
    }


}
