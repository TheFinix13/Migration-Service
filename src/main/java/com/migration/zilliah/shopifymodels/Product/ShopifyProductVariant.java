package com.migration.zilliah.shopifymodels.Product;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShopifyProductVariant {
    @JsonProperty("barcode")
    private String barcode;

    @JsonProperty("compare_at_price")
    private String compareAtPrice;

    @JsonProperty("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime createdAt;

    @JsonProperty("fulfillment_service")
    private String fulfillmentService;

    @JsonProperty("grams")
    private int grams;

    @JsonProperty("weight")
    private double weight;

    @JsonProperty("weight_unit")
    private String weightUnit;

    @JsonProperty("id")
    private long id;

    @JsonProperty("inventory_item_id")
    private long inventoryItemId;

    @JsonProperty("inventory_management")
    private String inventoryManagement;

    @JsonProperty("inventory_policy")
    private String inventoryPolicy;

    @JsonProperty("inventory_quantity")
    private int inventoryQuantity;

    @JsonProperty("option1")
    private String option1;

    @JsonProperty("position")
    private int position;

    @JsonProperty("price")
    private String price;

    @JsonProperty("product_id")
    private long productId;

    @JsonProperty("requires_shipping")
    private boolean requiresShipping;

    @JsonProperty("sku")
    private String sku;

    @JsonProperty("taxable")
    private boolean taxable;

    @JsonProperty("title")
    private String title;

    @JsonProperty("updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime updatedAt;

}