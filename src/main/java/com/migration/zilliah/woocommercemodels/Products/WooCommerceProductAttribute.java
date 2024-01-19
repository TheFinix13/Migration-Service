package com.migration.zilliah.woocommercemodels.Products;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class WooCommerceProductAttribute {
    @JsonProperty("id")
    private int id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("position")
    private int position;

    @JsonProperty("visible")
    private boolean visible;

    @JsonProperty("variation")
    private boolean variation;

    @JsonProperty("options")
    private List<String> options;
}
