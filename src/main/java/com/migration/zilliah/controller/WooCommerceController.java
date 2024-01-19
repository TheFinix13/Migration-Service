package com.migration.zilliah.controller;

import com.migration.zilliah.APIClient.WooCommerceAPIClient;
import com.migration.zilliah.woocommercemodels.Customers.WooCommerceCustomer;
import com.migration.zilliah.woocommercemodels.Products.WooCommerceProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/woocommerce")
public class WooCommerceController {

    private final WooCommerceAPIClient wooCommerceAPIClient;
    private static final Logger log = LoggerFactory.getLogger(WooCommerceController.class);

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
//    @PostMapping("/update-customer_username")
//    public ResponseEntity<String> updateCustomerUsername(@RequestParam int customerId, @RequestParam String newUsername) {
//        wooCommerceAPIClient.updateCustomerUsername(customerId, newUsername);
//        return ResponseEntity.ok("Username update process initiated.");
//    }

    @GetMapping("/batch_update_customer_username")
    public ResponseEntity<String> updateCustomerUsernames() {
        try {
            wooCommerceAPIClient.batchUpdateUsernames();
            String response = "Batch update completed";
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            String errorResponse = "Error during batch update: " + e.getMessage();
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete_customer/{customerId}")
    public void deleteCustomer(@PathVariable long customerId) {
        wooCommerceAPIClient.deleteWooCommerceCustomer(customerId);
    }
    @DeleteMapping("/batch_delete_customers")
    public void batchDeleteCustomers() {
        wooCommerceAPIClient.batchDeleteCustomers();
    }

    @PostMapping("/create_woocommerce_products")
    public ResponseEntity<WooCommerceProduct> createWooCommerceProduct(@RequestBody WooCommerceProduct product) {
        log.info("Received request to create WooCommerce product: {}", product.getName());
        WooCommerceProduct createdProduct = wooCommerceAPIClient.createWooCommerceProduct(product);
        log.info("Created WooCommerce product: {}", createdProduct.getName());
        return ResponseEntity.ok(createdProduct);
    }

    @GetMapping("get_woocommerce_products")
    public List<WooCommerceProduct> getAllWooCommerceProducts() {
        return wooCommerceAPIClient.getAllWooCommerceProducts();
    }

}

