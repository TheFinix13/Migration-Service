package com.migration.zilliah.shopifymodels.Customer;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ShopifyCustomer {

    @JsonProperty("id")
    private long id;

    @JsonProperty("email")
    private String email;

    @JsonProperty("accepts_marketing")
    private boolean acceptsMarketing;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("updated_at")
    private String updatedAt;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("orders_count")
    private int ordersCount;

    @JsonProperty("state")
    private String state;

    @JsonProperty("total_spent")
    private String totalSpent;

    @JsonProperty("last_order_id")
    private Long lastOrderId;

    @JsonProperty("note")
    private String note;

    @JsonProperty("verified_email")
    private boolean verifiedEmail;

    @JsonProperty("multipass_identifier")
    private String multipassIdentifier;

    @JsonProperty("tax_exempt")
    private boolean taxExempt;

    @JsonProperty("tags")
    private String tags;

    @JsonProperty("last_order_name")
    private String lastOrderName;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("addresses")
    private List<ShopifyAddress> addresses;

    @JsonProperty("accepts_marketing_updated_at")
    private String acceptsMarketingUpdatedAt;

    @JsonProperty("marketing_opt_in_level")
    private String marketingOptInLevel;

    @JsonProperty("tax_exemptions")
    private List<String> taxExemptions;

    @JsonProperty("email_marketing_consent")
    private ShopifyEmailMarketingConsent emailMarketingConsent;

    @JsonProperty("sms_marketing_consent")
    private ShopifySmsMarketingConsent smsMarketingConsent;

    @JsonProperty("default_address")
    private ShopifyAddress defaultAddress;

    @JsonProperty("metafield")
    private ShopifyMetaField metafield;

    @JsonProperty("password")
    private String password;

    @JsonProperty("password_confirmation")
    private String passwordConfirmation;


    // Constructors, getters, and setters

    // Add constructors, getters, and setters here...
}
