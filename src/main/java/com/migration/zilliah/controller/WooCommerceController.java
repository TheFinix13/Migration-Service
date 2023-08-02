package com.migration.zilliah.controller;

import com.migration.zilliah.APIClient.WooCommerceAPIClient;
import com.migration.zilliah.woocommercemodels.Customers.WooCommerceCustomer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@RestController
@RequestMapping("/api/woocommerce")
public class WooCommerceController {

    private final WooCommerceAPIClient wooCommerceAPIClient;

    @Autowired
    public WooCommerceController(WooCommerceAPIClient wooCommerceAPIClient) {
        this.wooCommerceAPIClient = wooCommerceAPIClient;
    }

    @GetMapping("/get_woocommerce_customer")
    public List<WooCommerceCustomer> getAllCustomers() {
        return wooCommerceAPIClient.getAllWooCommerceCustomers();
    }

    @PostMapping("/create_woocommerce_customer")
    public ResponseEntity<?> createWooCommerceCustomer(@RequestBody WooCommerceCustomer customer) {
        try {
            WooCommerceCustomer createdCustomer = wooCommerceAPIClient.createWooCommerceCustomer(customer);
            return new ResponseEntity<>(createdCustomer, HttpStatus.CREATED);
        } catch (HttpClientErrorException.Unauthorized e) {
            return new ResponseEntity<>("Invalid WooCommerce API credentials.", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to create customer: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

