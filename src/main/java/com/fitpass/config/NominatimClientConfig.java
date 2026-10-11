package com.fitpass.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class NominatimClientConfig {

    @Value("${nominatim.url}")
    private String nominatimUrl;

    @Value("${nominatim.user-agent}")
    private String userAgent;

    @Bean(name = "nominatimRestClient")
    public RestClient nominatimRestClient(RestClient.Builder builder) {
        return builder
                .baseUrl(nominatimUrl)
                .defaultHeader("User-Agent", userAgent)
                .defaultHeader("Accept", "application/json")
                .defaultHeader("Accept-Language", "es") // Hace que Nominatim responda en español
                .build();
    }
}