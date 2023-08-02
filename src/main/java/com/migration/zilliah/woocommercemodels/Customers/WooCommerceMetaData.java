package com.migration.zilliah.woocommercemodels.Customers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WooCommerceMetaData {

    @JsonProperty("id")
    private int id;

    @JsonProperty("key")
    private String key;

    @JsonProperty("value")
    private String value;

    // Constructors, getters, and setters

    // Add constructors, getters, and setters here...
}
