package com.migration.zilliah.APIClient;

import com.migration.zilliah.responses.ShopifyCustomerResponse;
import com.migration.zilliah.shopifymodels.Customer.ShopifyCustomer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;


@Component
public class ShopifyAPIClient {
    private final RestTemplate restTemplate;
    private final String shopifyApiUrl;
    private final String accessToken;

    public ShopifyAPIClient(RestTemplate restTemplate,
                            @Value("${shopify.api.url}") String shopifyApiUrl,
                            @Value("${shopify.api.accessToken}") String accessToken) {
        this.restTemplate = restTemplate;
        this.shopifyApiUrl = shopifyApiUrl;
        this.accessToken = accessToken;
    }

    public List<ShopifyCustomer> getShopifyCustomers() {
        String fullUrl = shopifyApiUrl + "/customers.json";
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Shopify-Access-Token", accessToken);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        ShopifyCustomerResponse response = restTemplate.exchange(fullUrl,
                        HttpMethod.GET,
                        requestEntity,
                        ShopifyCustomerResponse.class)
                .getBody();

        if (response != null && response.getCustomers() != null) {
            return response.getCustomers();
        } else {
            return Collections.emptyList();
        }
    }


}
