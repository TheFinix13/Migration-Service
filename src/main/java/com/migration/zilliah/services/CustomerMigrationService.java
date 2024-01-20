package com.migration.zilliah.services;

import com.migration.zilliah.APIClient.ShopifyAPIClient;
import com.migration.zilliah.APIClient.WooCommerceAPIClient;
import com.migration.zilliah.shopifymodels.Customer.ShopifyAddress;
import com.migration.zilliah.shopifymodels.Customer.ShopifyCustomer;
import com.migration.zilliah.woocommercemodels.Customers.WooCommerceAddress;
import com.migration.zilliah.woocommercemodels.Customers.WooCommerceCustomer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class CustomerMigrationService {

    private static final Logger log = LoggerFactory.getLogger(CustomerMigrationService.class);
    private final ShopifyAPIClient shopifyApiClient;
    private final WooCommerceAPIClient wooCommerceApiClient;

    @Autowired
    public CustomerMigrationService(ShopifyAPIClient shopifyApiClient, WooCommerceAPIClient wooCommerceApiClient) {
        this.shopifyApiClient = shopifyApiClient;
        this.wooCommerceApiClient = wooCommerceApiClient;
    }

    private WooCommerceAddress getWoocommerceAddress(ShopifyAddress shopifyAddress, ShopifyCustomer shopifyCustomer) {
        if (shopifyAddress == null) {
            return null;
        }

        WooCommerceAddress woocommerceAddress = new WooCommerceAddress();
        woocommerceAddress.setFirstName(shopifyAddress.getFirstName());
        woocommerceAddress.setLastName(shopifyAddress.getLastName());
        woocommerceAddress.setCompany(shopifyAddress.getCompany());
        woocommerceAddress.setAddress1(shopifyAddress.getAddress1());
        woocommerceAddress.setAddress2(shopifyAddress.getAddress2());
        woocommerceAddress.setCity(shopifyAddress.getCity());
        woocommerceAddress.setState(shopifyAddress.getProvinceCode());
        woocommerceAddress.setPostcode(shopifyAddress.getZip());
        woocommerceAddress.setCountry(shopifyAddress.getCountryCode());

        if (shopifyCustomer != null) {
            woocommerceAddress.setEmail(shopifyCustomer.getEmail());
            woocommerceAddress.setPhone(shopifyAddress.getPhone());
        }

        return woocommerceAddress;
    }

    public void migrateCustomers() {
        log.info("Starting customer migration...");
        try {
            // Fetch customers from Shopify
            List<ShopifyCustomer> shopifyCustomers = shopifyApiClient.getShopifyCustomers();

            // Migrate each customer to WooCommerce
            List<CompletableFuture<Void>> futures = shopifyCustomers.parallelStream()
                    .map(shopifyCustomer -> CompletableFuture.runAsync(() -> migrateCustomer(shopifyCustomer)))
                    .toList();

            // Wait for all futures to complete
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        } catch (Exception e) {
            // Log any error during fetching customers from Shopify
            log.error("Error fetching customers from Shopify: {}", e.getMessage());
        }
        log.info("Customer migration completed.");
    }

    private void migrateCustomer(ShopifyCustomer shopifyCustomer) {
        if (shopifyCustomer.getEmail() == null) {
            shopifyCustomer.setEmail(generateUniqueEmail()); // Generate a unique email for customers with null email
        }

        try {
            // Check if the customer with the same email already exists in WooCommerce
            WooCommerceCustomer existingCustomer = wooCommerceApiClient.getCustomerByEmail(shopifyCustomer.getEmail());

            if (existingCustomer != null) {
                // If the customer exists, you can choose to skip or update the existing customer
                log.info("Customer '{}' already exists in WooCommerce. Skipping migration.", shopifyCustomer.getEmail());
                return;
            }

            // Transform ShopifyCustomer to WooCommerceCustomer
            WooCommerceCustomer wooCommerceCustomer = transformToWooCommerceCustomer(shopifyCustomer);

            // Migrate the customer to WooCommerce
            wooCommerceApiClient.createWooCommerceCustomer(wooCommerceCustomer);

            // Log successful migration
            log.info("Customer '{}' migrated to WooCommerce successfully.", wooCommerceCustomer.getEmail());
        } catch (Exception e) {
            // Log any error encountered during migration
            log.error("Error migrating customer '{}' to WooCommerce: {}", shopifyCustomer.getEmail(), e.getMessage());
        }
    }

    private WooCommerceCustomer transformToWooCommerceCustomer(ShopifyCustomer shopifyCustomer) {
        // Create a new WooCommerceCustomer and map the properties accordingly
        WooCommerceCustomer wooCommerceCustomer = new WooCommerceCustomer();
//      wooCommerceCustomer.setId(shopifyCustomer.getId());
        wooCommerceCustomer.setEmail(shopifyCustomer.getEmail());
        wooCommerceCustomer.setFirstName(shopifyCustomer.getFirstName());
        wooCommerceCustomer.setLastName(shopifyCustomer.getLastName());
        wooCommerceCustomer.setUsername(shopifyCustomer.getFirstName() + "." + shopifyCustomer.getLastName());
        wooCommerceCustomer.setPassword(shopifyCustomer.getPassword());

        String shopifyCreatedAt = shopifyCustomer.getCreatedAt();
        LocalDateTime woocommerceDateCreated = DateConverter.convertShopifyDate(shopifyCreatedAt);
        wooCommerceCustomer.setDateCreated(woocommerceDateCreated);

        String shopifyUpdatedAt = shopifyCustomer.getUpdatedAt();
        LocalDateTime woocommerceDateModified = DateConverter.convertShopifyDate(shopifyUpdatedAt);
        wooCommerceCustomer.setDateModified(woocommerceDateModified);

        // Map billing and shipping addresses
        if (shopifyCustomer.getAddresses() != null && !shopifyCustomer.getAddresses().isEmpty()) {
            // Assume that the first address is used as the default shipping address
            ShopifyAddress shopifyAddress = shopifyCustomer.getAddresses().get(0);
            if (shopifyAddress != null) {
                WooCommerceAddress woocommerceBillingAddress = getWoocommerceAddress(shopifyAddress, shopifyCustomer);
                wooCommerceCustomer.setBilling(woocommerceBillingAddress);

                WooCommerceAddress woocommerceShippingAddress = getWoocommerceAddress(shopifyAddress, null);
                wooCommerceCustomer.setShipping(woocommerceShippingAddress);
            }
        }

        // Generate a random string (you can use UUID.randomUUID().toString() for better uniqueness)
        String randomString = Long.toHexString(Double.doubleToLongBits(Math.random()));
        // Update the username to be unique for customers with normal names
        if (shopifyCustomer.getFirstName() != null && shopifyCustomer.getLastName() != null) {
            wooCommerceCustomer.setUsername(shopifyCustomer.getFirstName() + "." + shopifyCustomer.getLastName());
        } else {
            // Update the username for customers with null names to something smaller
            wooCommerceCustomer.setUsername("nullcustomer" + randomString);
        }
        return wooCommerceCustomer;

    }

    // Helper method to generate unique email addresses
    private String generateUniqueEmail() {
        String domain = "example.com"; // Replace with your desired domain name
        UUID uuid = UUID.randomUUID();
        return "nullcustomer_" + uuid + "@" + domain;
    }


    private static class DateConverter {
        public static LocalDateTime convertShopifyDate(String shopifyDate) {
            // Define the formatter for the Shopify date format
            DateTimeFormatter shopifyFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

            // Parse the Shopify date string to a LocalDateTime object
            return LocalDateTime.parse(shopifyDate, shopifyFormatter);
        }
    }

    //    private static WooCommerceAddress getWoocommerceShippingAddress(ShopifyAddress billingAddress) {
//        // Map other shipping address properties as needed
//        WooCommerceAddress woocommerceShippingAddress = new WooCommerceAddress();
//        woocommerceShippingAddress.setFirstName(billingAddress.getFirstName());
//        woocommerceShippingAddress.setLastName(billingAddress.getLastName());
//        woocommerceShippingAddress.setCompany(billingAddress.getCompany());
//        woocommerceShippingAddress.setAddress1(billingAddress.getAddress1());
//        woocommerceShippingAddress.setAddress2(billingAddress.getAddress2());
//        woocommerceShippingAddress.setCity(billingAddress.getCity());
//        woocommerceShippingAddress.setState(billingAddress.getProvinceCode());
//        woocommerceShippingAddress.setPostcode(billingAddress.getZip());
//        woocommerceShippingAddress.setCountry(billingAddress.getCountryCode());
//        return woocommerceShippingAddress;
//    }
//
//    private static WooCommerceAddress getWoocommerceBillingAddress(ShopifyAddress billingAddress, ShopifyCustomer shopifyCustomer) {
//        // Map other billing address properties as needed
//        WooCommerceAddress woocommerceBillingAddress = new WooCommerceAddress();
//        woocommerceBillingAddress.setFirstName(billingAddress.getFirstName());
//        woocommerceBillingAddress.setLastName(billingAddress.getLastName());
//        woocommerceBillingAddress.setAddress1(billingAddress.getAddress1());
//        woocommerceBillingAddress.setAddress2(billingAddress.getAddress2());
//        woocommerceBillingAddress.setCity(billingAddress.getCity());
//        woocommerceBillingAddress.setState(billingAddress.getProvinceCode());
//        woocommerceBillingAddress.setPostcode(billingAddress.getZip());
//        woocommerceBillingAddress.setCountry(billingAddress.getCountryCode());
//        woocommerceBillingAddress.setEmail(shopifyCustomer.getEmail());
//        woocommerceBillingAddress.setPhone(billingAddress.getPhone());
//        return woocommerceBillingAddress;
//    }

//    public void migrateCustomers() {
//        log.info("Starting customer migration...");
//        try {
//            // Fetch customers from Shopify
//            List<ShopifyCustomer> shopifyCustomers = shopifyApiClient.getShopifyCustomers();
//
//            // Migrate each customer to WooCommerce
//            for (ShopifyCustomer shopifyCustomer : shopifyCustomers) {
//                if (shopifyCustomer.getEmail() == null) {
//                    shopifyCustomer.setEmail(generateUniqueEmail()); // Generate a unique email for customers with null email
//                }
//
//                try {
//                    // Check if the customer with the same email already exists in WooCommerce
//                    WooCommerceCustomer existingCustomer = wooCommerceApiClient.getCustomerByEmail(shopifyCustomer.getEmail());
//
//                    if (existingCustomer != null) {
//                        // If the customer exists, you can choose to skip or update the existing customer
//                        log.info("Customer '{}' already exists in WooCommerce. Skipping migration.", shopifyCustomer.getEmail());
//                        continue;
//                    }
//
//                    // Transform ShopifyCustomer to WooCommerceCustomer
//                    WooCommerceCustomer wooCommerceCustomer = transformToWooCommerceCustomer(shopifyCustomer);
//
//                    // Migrate the customer to WooCommerce
//                    wooCommerceApiClient.createWooCommerceCustomer(wooCommerceCustomer);
//
//                    // Log successful migration
//                    log.info("Customer '{}' migrated to WooCommerce successfully.", wooCommerceCustomer.getEmail());
//                } catch (Exception e) {
//                    // Log any error encountered during migration
//                    log.error("Error migrating customer '{}' to WooCommerce: {}", shopifyCustomer.getEmail(), e.getMessage());
//                }
//            }
//        } catch (Exception e) {
//            // Log any error during fetching customers from Shopify
//            log.error("Error fetching customers from Shopify: {}", e.getMessage());
//        }
//        log.info("Customer migration completed.");
//    }
//

}
