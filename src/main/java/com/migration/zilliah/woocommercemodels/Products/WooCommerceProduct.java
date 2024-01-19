package com.migration.zilliah.woocommercemodels.Products;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WooCommerceProduct {
    @JsonProperty("id")
    private int id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("slug")
    private String slug;

    @JsonProperty("date_created")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateCreated;

    @JsonProperty("date_modified")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateModified;

    @JsonProperty("description")
    private String description;

    @JsonProperty("short_description")
    private String shortDescription;

    @JsonProperty("sku")
    private String sku;

    @JsonProperty("price")
    private String price;

    @JsonProperty("regular_price")
    private String regularPrice;

    @JsonProperty("sale_price")
    private String salePrice;

    @JsonProperty("on_sale")
    private boolean onSale;

    @JsonProperty("purchasable")
    private boolean purchasable;

    @JsonProperty("virtual")
    private boolean virtual;

    @JsonProperty("downloadable")
    private boolean downloadable;

    @JsonProperty("downloads")
    private List<WooCommerceProductDownload> downloads;

    @JsonProperty("external_url")
    private String externalUrl;

    @JsonProperty("tax_status")
    private String taxStatus;

    @JsonProperty("tax_class")
    private String taxClass;

    @JsonProperty("manage_stock")
    private boolean manageStock;

    @JsonProperty("stock_quantity")
    private int stockQuantity;

    @JsonProperty("stock_status")
    private String stockStatus;

    @JsonProperty("back_orders")
    private String backorders;

    @JsonProperty("back_orders_allowed")
    private boolean backordersAllowed;

    @JsonProperty("sold_individually")
    private boolean soldIndividually;

    @JsonProperty("weight")
    private String weight;

    @JsonProperty("length")
    private String length;

    @JsonProperty("width")
    private String width;

    @JsonProperty("height")
    private String height;

    @JsonProperty("dimensions")
    private WooCommerceProductDimension dimension;

    @JsonProperty("shipping_required")
    private boolean shippingRequired;

    @JsonProperty("shipping_taxable")
    private boolean shippingTaxable;

    @JsonProperty("shipping_class")
    private String shippingClass;

    @JsonProperty("reviews_allowed")
    private boolean reviewsAllowed;

    @JsonProperty("average_rating")
    private String averageRating;

    @JsonProperty("rating_count")
    private int ratingCount;

    @JsonProperty("menu_order")
    private int menuOrder;

    @JsonProperty("parent_id")
    private int parentId;

    @JsonProperty("purchase_note")
    private String purchaseNote;

    @JsonProperty("categories")
    private List<WooCommerceProductCategory> categories;

    @JsonProperty("tags")
    private List<WooCommerceProductTag> tags;

    @JsonProperty("related_ids")
    private List<Integer> relatedIds;

    @JsonProperty("upsell_ids")
    private List<Integer> upsellIds;

    @JsonProperty("crossSell_ids")
    private List<Integer> crossSellIds;

    @JsonProperty("variations")
    private List<Integer> variations;

    @JsonProperty("grouped_products")
    private List<Integer> groupedProducts;

    @JsonProperty("images")
    private List<WooCommerceProductImage> images;

    @JsonProperty("attributes")
    private List<WooCommerceProductAttribute> attributes;

    @JsonProperty("default_attributes")
    private List<WooCommerceProductAttribute> defaultAttributes;

    @JsonProperty("meta_data")
    private List<WooCommerceProductMeta> metaData;

    // Getters and setters
}
