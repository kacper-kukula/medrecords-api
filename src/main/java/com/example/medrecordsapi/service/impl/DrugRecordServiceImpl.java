package com.example.medrecordsapi.service.impl;

import com.example.medrecordsapi.dto.drugrecord.DrugRecordResponseDto;
import com.example.medrecordsapi.exception.custom.DrugRecordNotFoundException;
import com.example.medrecordsapi.exception.custom.EntityNotFoundException;
import com.example.medrecordsapi.mapper.DrugRecordMapper;
import com.example.medrecordsapi.model.DrugRecord;
import com.example.medrecordsapi.repository.DrugRecordRepository;
import com.example.medrecordsapi.service.DrugRecordService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class DrugRecordServiceImpl implements DrugRecordService {

    private static final String FDA_BASE_URL = "https://api.fda.gov/drug/drugsfda.json";
    private static final String MANUFACTURER_NAME_FIELD = "openfda.manufacturer_name:";
    private static final String BRAND_NAME_FIELD = "openfda.brand_name:";
    private static final String APPLICATION_NUMBER_FIELD = "openfda.application_number:";
    private static final String API_KEY_PARAM_NAME = "api_key";
    private static final String SEARCH_PARAM_NAME = "search";
    private static final String LIMIT_PARAM_NAME = "limit";
    private static final String SKIP_PARAM_NAME = "skip";
    private static final String AND_OPERATOR = "+AND+";

    private final DrugRecordRepository drugRecordRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final DrugRecordMapper drugRecordMapper;
    private final String apiKey = System.getenv("FDA_API_KEY");

    @Override
    public JsonNode searchDrugRecords(String manufacturerName, String brandName,
                                      int page, int size) throws JsonProcessingException {
        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromUriString(FDA_BASE_URL);
        StringBuilder searchQuery = new StringBuilder()
                .append(MANUFACTURER_NAME_FIELD)
                .append(manufacturerName);

        appendApiKeyIfExists(urlBuilder);
        appendBrandNameIfExists(brandName, searchQuery);

        String url = urlBuilder.queryParam(SEARCH_PARAM_NAME, searchQuery.toString())
                .queryParam(LIMIT_PARAM_NAME, size)
                .queryParam(SKIP_PARAM_NAME, (page - 1) * size)
                .toUriString();

        String rawJson = restTemplate.getForObject(url, String.class);

        return objectMapper.readTree(rawJson);
    }

    @Override
    public DrugRecordResponseDto saveDrugRecord(String applicationNumber) throws JsonProcessingException {
        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromUriString(FDA_BASE_URL);

        String url = urlBuilder
                .queryParam(SEARCH_PARAM_NAME, APPLICATION_NUMBER_FIELD + applicationNumber)
                .toUriString();

        String rawJson;

        try {
            rawJson = restTemplate.getForObject(url, String.class);
        } catch (RestClientResponseException e) {
            throw new DrugRecordNotFoundException("No drug records found for the given Application Number: "
                    + applicationNumber);
        }

        JsonNode rootNode = objectMapper.readTree(rawJson);
        JsonNode result = rootNode.path("results").get(0);
        JsonNode openFda = result.path("openfda");

        String manufacturerName = openFda.path("manufacturer_name").get(0).asText();
        String substanceName = openFda.path("substance_name").get(0).asText();

        List<String> productNumbers = new ArrayList<>();

        openFda.path("product_ndc").forEach(node -> productNumbers.add(node.asText()));

        DrugRecord drugRecord = new DrugRecord(applicationNumber, manufacturerName,
                substanceName, productNumbers);
        DrugRecord savedDrugRecord = drugRecordRepository.save(drugRecord);

        return drugRecordMapper.toDto(savedDrugRecord);
    }

    @Override
    public DrugRecord findByApplicationNumber(String applicationNumber) {
        return drugRecordRepository.findByApplicationNumber(applicationNumber)
                .orElseThrow(() -> new EntityNotFoundException("Not Found"));
    }

    @Override
    public Page<DrugRecord> findAllByManufacturerName(
            String manufacturerName, Pageable pageable) {
        return drugRecordRepository.findByManufacturerName(manufacturerName, pageable);
    }

    @Override
    public Page<DrugRecord> findAllBySubstanceName(
            String substanceName, Pageable pageable) {
        return drugRecordRepository.findBySubstanceName(substanceName, pageable);
    }

    @Override
    public Page<DrugRecord> findAllByProductNumbersContaining(
            String productNumber, Pageable pageable) {
        return drugRecordRepository.findByProductNumbersContaining(productNumber, pageable);

    }

    @Override
    public Page<DrugRecord> findAllDrugRecords(Pageable pageable) {
        return drugRecordRepository.findAll(pageable);
    }

    private void appendBrandNameIfExists(String brandName, StringBuilder searchQuery) {
        if (brandName != null && !brandName.isEmpty()) {
            searchQuery.append(AND_OPERATOR)
                    .append(BRAND_NAME_FIELD)
                    .append(brandName);
        }
    }

    private void appendApiKeyIfExists(UriComponentsBuilder urlBuilder) {
        if (apiKey != null && !apiKey.isEmpty()) {
            urlBuilder.queryParam(API_KEY_PARAM_NAME, apiKey);
        }
    }
}
