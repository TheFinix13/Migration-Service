package com.migration.zilliah.woocommercemodels.Products;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class WooCommerceProductCategory {
    @JsonProperty("id")
    private int id;

    @JsonProperty("category_name")
    private String name;

    @JsonProperty("category_slug")
    private String slug;

}
