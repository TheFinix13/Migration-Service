package com.migration.zilliah.shopifymodels.Product;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;


@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShopifyProduct {
    @JsonProperty("body_html")
    private String bodyHtml;

    @JsonProperty("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime createdAt;

    @JsonProperty("handle")
    private String handle;

    @JsonProperty("id")
    private long id;

    @JsonProperty("images")
    private List<ShopifyProductImage> images;

    @JsonProperty("options")
    private List<ShopifyProductOptions> options;

    @JsonProperty("product_type")
    private String productType;

    @JsonProperty("published_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime publishedAt;

    @JsonProperty("published_scope")
    private String publishedScope;

    @JsonProperty("status")
    private String status;

    @JsonProperty("tags")
    private String tags;

    @JsonProperty("template_suffix")
    private String templateSuffix;

    @JsonProperty("title")
    private String title;

    @JsonProperty("updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime updatedAt;

    @JsonProperty("variants")
    private List<ShopifyProductVariant> variants;

    @JsonProperty("vendor")
    private String vendor;

    // Getters and setters
}

