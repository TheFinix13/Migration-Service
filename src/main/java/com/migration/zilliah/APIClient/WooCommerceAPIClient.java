package com.migration.zilliah.APIClient;

import com.migration.zilliah.woocommercemodels.Customers.WooCommerceCustomer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

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
                log.error("Failed to create WooCommerce customer. Response status: {}", responseEntity.getStatusCode());
                return null;
            }
        } catch (RestClientException e) {
            log.error("Error creating WooCommerce customer: {}", e.getMessage());
            return null;
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
            WooCommerceCustomer[] customersArray = responseEntity.getBody();
            assert customersArray != null;
            return List.of(customersArray);
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

    // Helper method to create HttpHeaders with API authentication
    private HttpHeaders createHeadersWithAuth() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setBasicAuth(apiKey, apiSecret);
        return headers;
    }

}