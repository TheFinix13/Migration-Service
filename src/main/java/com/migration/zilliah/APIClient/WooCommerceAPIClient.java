package com.migration.zilliah.APIClient;

import com.migration.zilliah.woocommercemodels.Customers.WooCommerceCustomer;
import com.migration.zilliah.woocommercemodels.Products.WooCommerceProduct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class WooCommerceAPIClient {
    private static final Logger log = LoggerFactory.getLogger(WooCommerceAPIClient.class);
    private final RestTemplate restTemplate;
    private final String woocommerceApiUrl;
    private final String apiKey;
    private final String apiSecret;

    public WooCommerceAPIClient(RestTemplate restTemplate,
                                @Value("${woocommerce.api.url}") String woocommerceApiUrl,
                                @Value("${woocommerce.api.key}") String apiKey,
                                @Value("${woocommerce.api.secret}") String apiSecret) {
        this.restTemplate = restTemplate;
        this.woocommerceApiUrl = woocommerceApiUrl;
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
    }

    public WooCommerceCustomer createWooCommerceCustomer(WooCommerceCustomer customer) {
        try {
            String url = woocommerceApiUrl + "/customers";

            HttpHeaders headers = createHeadersWithAuth();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<WooCommerceCustomer> requestEntity = new HttpEntity<>(customer, headers);

            ResponseEntity<WooCommerceCustomer> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    WooCommerceCustomer.class);

            if (responseEntity.getStatusCode() == HttpStatus.CREATED) {
                return responseEntity.getBody();
            } else {
                log.error("Failed to create WooCommerce customer with email {}. Response status: {}", customer.getEmail(), responseEntity.getStatusCode());
                throw new RuntimeException("Failed to create WooCommerce customer");
            }
        } catch (RestClientException e) {
            log.error("Error creating WooCommerce customer with email {}: {}", customer.getEmail(), e.getMessage());
            throw new RuntimeException("Failed to create WooCommerce customer", e);
        }
    }

    public List<WooCommerceCustomer> getAllWooCommerceCustomers() {
        String url = woocommerceApiUrl + "/customers";

        HttpHeaders headers = createHeadersWithAuth();

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<WooCommerceCustomer[]> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                WooCommerceCustomer[].class
        );

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            WooCommerceCustomer[] customerArray = responseEntity.getBody();
            assert customerArray != null;
            return List.of(customerArray);
        } else {
            // Handle the error scenario if needed
            return Collections.emptyList();
        }
    }

    public WooCommerceCustomer getCustomerByEmail(String email) {
        String url = woocommerceApiUrl + "/customers";

        HttpHeaders headers = createHeadersWithAuth();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create a UriComponentsBuilder to build the URL with the email query parameter
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("email", email);

        // Create a request entity with an empty body and the headers
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);


        // Send a GET request to fetch customers with the specified email
        ResponseEntity<WooCommerceCustomer[]> responseEntity = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                requestEntity,
                WooCommerceCustomer[].class
        );

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            WooCommerceCustomer[] customers = responseEntity.getBody();
            if (customers != null && customers.length > 0) {
                // If the response contains customers, return the first one (assuming email is unique)
                return customers[0];
            }
        }

        // Return null if no customer with the specified email is found
        return null;
    }

    public WooCommerceCustomer getCustomerById(long customerId) {
        try {
            String url = woocommerceApiUrl + "/customers/" + customerId;

            HttpHeaders headers = createHeadersWithAuth();

            HttpEntity<String> requestEntity = new HttpEntity<>(headers);

            ResponseEntity<WooCommerceCustomer> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    requestEntity,
                    WooCommerceCustomer.class
            );

            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                return responseEntity.getBody();
            } else {
                log.error("Failed to get WooCommerce customer by ID {}. Response status: {}", customerId, responseEntity.getStatusCode());
                return null;
            }
        } catch (RestClientException e) {
            log.error("Error getting WooCommerce customer by ID {}: {}", customerId, e.getMessage());
            return null;
        }
    }

//    public void updateWooCommerceCustomerUsername(int customerId, String newUsername) {
//        try {
//            String url = woocommerceApiUrl + "/customers/" + customerId;
//
//            HttpHeaders headers = createHeadersWithAuth();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//
//            WooCommerceCustomer customerUpdate = new WooCommerceCustomer();
//            customerUpdate.setUsername(newUsername);
//
//            HttpEntity<WooCommerceCustomer> requestEntity = new HttpEntity<>(customerUpdate, headers);
//
//            ResponseEntity<Void> responseEntity = restTemplate.exchange(
//                    url,
//                    HttpMethod.PUT,
//                    requestEntity,
//                    Void.class);
//
//            if (responseEntity.getStatusCode() == HttpStatus.OK) {
//                log.info("Username for customer with ID {} has been successfully updated to '{}'.", customerId, newUsername);
//            } else {
//                log.error("Failed to update username for customer with ID {}. Response status: {}", customerId, responseEntity.getStatusCode());
//            }
//        } catch (RestClientException e) {
//            log.error("Error updating username for customer with ID {}: {}", customerId, e.getMessage());
//        }
//    }

    public void batchUpdateUsernames() {
        log.info("Starting batchUpdateUsernames...");

        List<WooCommerceCustomer> customers = getAllWooCommerceCustomers();

        log.info("Total customers fetched: {}", customers.size());

        int updatedCount = 0;
        int nullUsernameCount = 1; // Initialize the counter for null usernames


        // Process customers for update and copy data
        for (WooCommerceCustomer customer : customers) {
            log.info("Processing customer: ID={}, Username={}", customer.getId(), customer.getUsername());

            if (shouldUpdateUsername(customer)) {
                String newUsername = generateNewUsername(customer, nullUsernameCount);

                // Copy relevant data from old customer
                WooCommerceCustomer newCustomer = new WooCommerceCustomer();
                copyCustomerData(customer, newCustomer);
                newCustomer.setUsername(newUsername);

                // Delete the old customer
                deleteWooCommerceCustomer(customer.getId());
                log.info("Deleted old customer: ID={}, Username={}", customer.getId(), customer.getUsername());

                // Create the new customer with copied data and updated username
                WooCommerceCustomer createdCustomer = createWooCommerceCustomer(newCustomer);
                if (createdCustomer != null) {
                    log.info("Successfully created new customer with ID {} and updated username '{}'", createdCustomer.getId(), createdCustomer.getUsername());
                    updatedCount++;
                    nullUsernameCount++;
                }
            }
        }

        log.info("Batch update completed. Total customers updated: {}", updatedCount);
    }

    private String generateNewUsername(WooCommerceCustomer customer, int count) {
        if (shouldUpdateUsername(customer)) {
            return "null" + count;
        } else {
            String firstName = customer.getFirstName() != null ? customer.getFirstName().toLowerCase() : "";
            String lastName = customer.getLastName() != null ? customer.getLastName().toLowerCase() : "";
            String username = firstName + "." + lastName;
            // Remove special characters except for periods, underscores, and hyphens
            username = username.replaceAll("[^a-zA-Z0-9._-]", "");
            return username;
        }
    }

    private boolean shouldUpdateUsername(WooCommerceCustomer customer) {
        String username = customer.getUsername();
        boolean shouldUpdate = username.toLowerCase().startsWith("null") || username.toLowerCase().contains("null");

        if (shouldUpdate) {
            log.info("Should update username for customer ID={}, Username={}", customer.getId(), customer.getUsername());
        } else {
            log.info("No need to update username for customer ID={}, Username={}", customer.getId(), customer.getUsername());
        }

        return shouldUpdate;
    }


    public void deleteWooCommerceCustomer(long customerId) {
        String url = woocommerceApiUrl + "/customers/" + customerId + "?force=true";

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(apiKey, apiSecret);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        try {
            restTemplate.exchange(url,
                    HttpMethod.DELETE,
                    requestEntity,
                    Void.class);

            log.info("Customer with ID {} has been successfully deleted.", customerId);
        } catch (HttpClientErrorException.NotFound e) {
            log.error("Customer with ID {} not found. Unable to delete.", customerId);
        } catch (HttpClientErrorException e) {
            log.error("Error deleting customer with ID {}: {}", customerId, e.getMessage());
        }
    }

    public void batchDeleteCustomers() {
        log.info("Starting batchDeleteCustomers...");

        List<WooCommerceCustomer> customers = getAllWooCommerceCustomers();

        log.info("Total customers fetched: {}", customers.size());

        int deletedCount = 0;

        for (WooCommerceCustomer customer : customers) {
            log.info("Deleting customer: ID={}, Email={}", customer.getId(), customer.getEmail());

            deleteWooCommerceCustomer(customer.getId());
            deletedCount++;
        }

        log.info("Batch delete completed. Total customers deleted: {}", deletedCount);
    }

    // Helper method to create HttpHeaders with API authentication
    private HttpHeaders createHeadersWithAuth() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        if (StringUtils.hasText(apiKey) && StringUtils.hasText(apiSecret)) {
            headers.setBasicAuth(apiKey, apiSecret);
        } else {
            throw new IllegalArgumentException("API key and/or API secret are missing or empty.");
        }
//        headers.setBasicAuth(apiKey, apiSecret);
        return headers;
    }

    // Helper method to copy old user information to the new
    private void copyCustomerData(WooCommerceCustomer source, WooCommerceCustomer destination) {
        Field[] fields = WooCommerceCustomer.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object value = field.get(source);
                field.set(destination, value);
            } catch (IllegalAccessException e) {
                log.error("Error copying customer data: {}", e.getMessage());
            }
        }
    }

    public WooCommerceProduct createWooCommerceProduct(WooCommerceProduct product) {
        String url = woocommerceApiUrl + "/products";

        HttpHeaders headers = createHeadersWithAuth();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<WooCommerceProduct> requestEntity = new HttpEntity<>(product, headers);

        try {
            // Send POST request to create the WooCommerce product
            return restTemplate.postForObject(url, requestEntity, WooCommerceProduct.class);
        } catch (Exception e) {
            log.error("Error occurred while creating WooCommerce product: {}", e.getMessage());
            throw new RuntimeException("Failed to create WooCommerce product", e);
        }
    }

    public List<WooCommerceProduct> getAllWooCommerceProducts() {
        String url = woocommerceApiUrl + "/products";

        HttpHeaders headers = createHeadersWithAuth();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<WooCommerceProduct[]> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                WooCommerceProduct[].class
        );

        return Arrays.asList(responseEntity.getBody());
    }

}