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
public class WooCommerceProductTag {
    @JsonProperty("id")
    private int id;

    @JsonProperty("tag_name")
    private String name;

    @JsonProperty("tag_slug")
    private String slug;

}
