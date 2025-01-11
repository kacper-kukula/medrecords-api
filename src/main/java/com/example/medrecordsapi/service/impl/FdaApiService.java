package com.example.medrecordsapi.service.impl;

import com.example.medrecordsapi.exception.custom.DrugRecordNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class FdaApiService {

    private static final String FDA_BASE_URL = "https://api.fda.gov/drug/drugsfda.json";
    private static final String API_KEY_PARAM_NAME = "api_key";
    private static final String SEARCH_PARAM_NAME = "search";
    private static final String LIMIT_PARAM_NAME = "limit";
    private static final String SKIP_PARAM_NAME = "skip";
    private static final int PAGE_SIZE_ONE = 1;
    private static final String NO_DRUGS_FOUND_ERROR = "No drug records found";

    private final RestTemplate restTemplate;
    private final String apiKey = System.getenv("FDA_API_KEY");

    public String fetchDrugData(String queryParam, String queryValue, int page, int size) {
        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromUriString(FDA_BASE_URL);

        // Append as first parameter only if FDA Api Key is provided in the .env file
        if (apiKey != null && !apiKey.isEmpty()) {
            urlBuilder.queryParam(API_KEY_PARAM_NAME, apiKey);
        }

        String uriString = urlBuilder.queryParam(SEARCH_PARAM_NAME, queryParam + queryValue)
                .queryParam(LIMIT_PARAM_NAME, size) // Pagination limit
                .queryParam(SKIP_PARAM_NAME, (page - PAGE_SIZE_ONE) * size) // Offset
                .toUriString();

        try {
            return restTemplate.getForObject(uriString, String.class);
        } catch (RestClientResponseException e) {
            throw new DrugRecordNotFoundException(NO_DRUGS_FOUND_ERROR);
        }
    }
}
