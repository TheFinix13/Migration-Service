package com.migration.zilliah.woocommercemodels.Customers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WooCommerceCustomer {

    @JsonProperty("id")
    private int id;

    @JsonProperty("date_created")
    private LocalDateTime dateCreated;

    @JsonProperty("date_created_gmt")
    private LocalDateTime dateCreatedGmt;

    @JsonProperty("date_modified")
    private LocalDateTime dateModified;

    @JsonProperty("date_modified_gmt")
    private LocalDateTime dateModifiedGmt;

    @JsonProperty("email")
    private String email;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("role")
    private String role;

    @JsonProperty("username")
    private String username;

    @JsonProperty("password")
    private String password;

    @JsonProperty("billing")
    private WooCommerceAddress billing;

    @JsonProperty("shipping")
    private WooCommerceAddress shipping;

    @JsonProperty("is_paying_customer")
    private boolean isPayingCustomer;

    @JsonProperty("avatar_url")
    private String avatarUrl;

    @JsonProperty("meta_data")
    private List<WooCommerceMetaData> metaData;
}
