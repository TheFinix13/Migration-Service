package com.migration.zilliah.controller;

import com.migration.zilliah.services.CustomerMigrationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/migration")
public class MigrationController {

    private final CustomerMigrationService customerMigrationService;

    public MigrationController(CustomerMigrationService customerMigrationService) {
        this.customerMigrationService = customerMigrationService;
    }

    @PostMapping("/customers")
    public ResponseEntity<String> migrateCustomers() {
        try {
            customerMigrationService.migrateCustomers();
            return ResponseEntity.ok("Customer migration from Shopify to WooCommerce completed successfully.");
        } catch (Exception e) {
            // Handle any exceptions or errors that occurred during migration
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during customer migration.");
        }
    }
}
