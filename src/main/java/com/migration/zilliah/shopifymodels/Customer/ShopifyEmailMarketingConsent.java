package com.migration.zilliah.shopifymodels.Customer;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;


@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ShopifyEmailMarketingConsent {

    @JsonProperty("state")
    private String state;

    @JsonProperty("opt_in_level")
    private String optInLevel;

    @JsonProperty("consent_updated_at")
    private String consentUpdatedAt;

    // Constructors, getters, and setters

    // Add constructors, getters, and setters here...
}
