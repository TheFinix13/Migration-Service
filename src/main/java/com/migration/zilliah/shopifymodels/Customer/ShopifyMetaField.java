package com.migration.zilliah.shopifymodels.Customer;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ShopifyMetaField {

    @JsonProperty("key")
    private String key;

    @JsonProperty("namespace")
    private String namespace;

    @JsonProperty("value")
    private String value;

    @JsonProperty("type")
    private String type;

    // Constructors, getters, and setters

    // Add constructors, getters, and setters here...
}

