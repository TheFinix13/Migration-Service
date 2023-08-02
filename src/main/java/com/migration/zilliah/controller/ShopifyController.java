package com.migration.zilliah.controller;

import com.migration.zilliah.APIClient.ShopifyAPIClient;
import com.migration.zilliah.shopifymodels.Customer.ShopifyCustomer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/shopify")
public class ShopifyController {

    private final ShopifyAPIClient shopifyAPIClient;

    @Autowired
    public ShopifyController(ShopifyAPIClient shopifyAPIClient) {
        this.shopifyAPIClient = shopifyAPIClient;
    }

    @GetMapping("/get_shopify_customers")
    public List<ShopifyCustomer> getShopifyCustomers() {
        return shopifyAPIClient.getShopifyCustomers();
    }
}
