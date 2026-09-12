package com.alura.literalura.client;

import com.alura.literalura.dto.GutenDexResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class GutendexClient {

    private final RestClient restClient;

    public GutendexClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public GutenDexResponse searchBooks(String query) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/books")
                        .queryParam("search", query)
                        .build())
                .retrieve()
                .body(GutenDexResponse.class);
    }
}
