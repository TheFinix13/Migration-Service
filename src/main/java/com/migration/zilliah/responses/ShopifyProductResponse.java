package com.migration.zilliah.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.migration.zilliah.shopifymodels.Product.ShopifyProduct;
import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShopifyProductResponse {
    @JsonProperty("products")
    private List<ShopifyProduct> products;

}