package com.migration.zilliah;

import com.migration.zilliah.APIClient.WooCommerceAPIClient;
import com.migration.zilliah.services.ProductMigrationService;
import com.migration.zilliah.woocommercemodels.Products.WooCommerceProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    private final ProductMigrationService migrationService;
    private final WooCommerceAPIClient woocommerceAPIClient;

    @Autowired
    public TestController(ProductMigrationService migrationService, WooCommerceAPIClient woocommerceAPIClient) {
        this.migrationService = migrationService;
        this.woocommerceAPIClient = woocommerceAPIClient;
    }

    @GetMapping("/migrate")
    public ResponseEntity<String> testMigration() {
        WooCommerceProduct sampleProduct = createSampleProduct(); // Create a sample product for testing
        WooCommerceProduct createdProduct = woocommerceAPIClient.createWooCommerceProduct(sampleProduct);

        // You can return the created product's ID or any other relevant information
        return ResponseEntity.ok("Product created with ID: " + createdProduct.getId());
    }

    private WooCommerceProduct createSampleProduct() {

        return new WooCommerceProduct();
    }
}

