package com.migration.zilliah.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.migration.zilliah.shopifymodels.Customer.ShopifyCustomer;
import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShopifyCustomerResponse {
    @JsonProperty("customers")
    private List<ShopifyCustomer> customers;
}
