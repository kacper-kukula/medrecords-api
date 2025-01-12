package com.example.medrecordsapi.service.impl;

import com.example.medrecordsapi.exception.custom.DrugRecordNotFoundException;
import com.example.medrecordsapi.service.FdaApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
@Slf4j
public class FdaApiServiceImpl implements FdaApiService {

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
        log.info("Fetching drug data with queryParam: {}, queryValue: {}, page: {}, size: {}",
                queryParam, queryValue, page, size);

        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromUriString(FDA_BASE_URL);

        // Append as first parameter only if FDA Api Key is provided in the .env file
        if (apiKey != null && !apiKey.isEmpty()) {
            urlBuilder.queryParam(API_KEY_PARAM_NAME, apiKey);
            log.info("Using API key for FDA API");
        }

        String uriString = urlBuilder.queryParam(SEARCH_PARAM_NAME, queryParam + queryValue)
                .queryParam(LIMIT_PARAM_NAME, size) // Pagination limit
                .queryParam(SKIP_PARAM_NAME, (page - PAGE_SIZE_ONE) * size) // Offset
                .toUriString();

        try {
            String response = restTemplate.getForObject(uriString, String.class);
            log.info("Received response from FDA API");
            return response;
        } catch (RestClientResponseException e) {
            log.error("Error occurred while fetching drug data from FDA API: {}",
                    e.getMessage());
            throw new DrugRecordNotFoundException(NO_DRUGS_FOUND_ERROR);
        }
    }
}
