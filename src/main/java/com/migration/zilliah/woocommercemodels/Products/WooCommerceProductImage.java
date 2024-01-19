package com.migration.zilliah.woocommercemodels.Products;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class WooCommerceProductImage {
    @JsonProperty("id")
    private int id;

    @JsonProperty("date_created")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateCreated;

    @JsonProperty("date_modified")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateModified;

    @JsonProperty("src")
    private String src;

    @JsonProperty("name")
    private String name;

    @JsonProperty("alt")
    private String alt;

    // Getters and setters
}
