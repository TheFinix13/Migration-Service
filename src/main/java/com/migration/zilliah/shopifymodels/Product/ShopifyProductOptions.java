package com.migration.zilliah.shopifymodels.Product;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShopifyProductOptions {
    @JsonProperty("id")
    private long id;

    @JsonProperty("product_id")
    private long productId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("position")
    private int position;

    @JsonProperty("values")
    private List<String> values;

    // Getters and setters
}
