package com.example.medrecordsapi.service.impl;

import com.example.medrecordsapi.dto.drugrecord.DrugRecordResponseDto;
import com.example.medrecordsapi.exception.custom.EntityNotFoundException;
import com.example.medrecordsapi.mapper.DrugRecordMapper;
import com.example.medrecordsapi.model.DrugRecord;
import com.example.medrecordsapi.repository.DrugRecordRepository;
import com.example.medrecordsapi.service.DrugRecordService;
import com.example.medrecordsapi.service.FdaApiService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DrugRecordServiceImpl implements DrugRecordService {

    private static final String MANUFACTURER_NAME_FIELD = "openfda.manufacturer_name:";
    private static final String BRAND_NAME_FIELD = "openfda.brand_name:";
    private static final String APPLICATION_NUMBER_FIELD = "openfda.application_number:";
    private static final String RESULTS_NODE_PATH = "results";
    private static final String OPENFDA_NODE_PATH = "openfda";
    private static final String PRODUCT_NDC_NODE_PATH = "product_ndc";
    private static final String MANUFACTURER_NAME_NODE_PATH = "manufacturer_name";
    private static final String SUBSTANCE_NAME_NODE_PATH = "substance_name";
    private static final String AND_OPERATOR = "+AND+";
    private static final int PAGE_SIZE_ONE = 1;
    private static final int INDEX_ZERO = 0;
    private static final String NO_DRUGS_FOUND_ERROR = "No drug records found";

    private final DrugRecordRepository drugRecordRepository;
    private final ObjectMapper objectMapper;
    private final DrugRecordMapper drugRecordMapper;
    private final FdaApiService fdaApiService;

    @Override
    public JsonNode searchDrugRecords(String manufacturerName, String brandName,
                                      int page, int size) throws JsonProcessingException {
        log.info("Searching for drug records with manufacturer: {}, brand: {}, page: {}, "
                + "size: {}", manufacturerName, brandName, page, size);

        StringBuilder searchQuery = new StringBuilder()
                .append(MANUFACTURER_NAME_FIELD)
                .append(manufacturerName);

        if (brandName != null && !brandName.isEmpty()) {
            searchQuery.append(AND_OPERATOR)
                    .append(BRAND_NAME_FIELD)
                    .append(brandName);
        }

        String rawJson = fdaApiService.fetchDrugData(
                "", searchQuery.toString(), page, size);
        JsonNode result = objectMapper.readTree(rawJson);
        log.info("Search result size: {}", result.path(RESULTS_NODE_PATH).size());

        return result;
    }

    @Override
    public DrugRecordResponseDto saveDrugRecord(String applicationNumber)
            throws JsonProcessingException {
        log.info("Attempting to save drug record with application number: {}", applicationNumber);

        String rawJson = fdaApiService.fetchDrugData(APPLICATION_NUMBER_FIELD, applicationNumber,
                PAGE_SIZE_ONE, PAGE_SIZE_ONE);

        JsonNode rootNode = objectMapper.readTree(rawJson);
        JsonNode result = rootNode.path(RESULTS_NODE_PATH).get(INDEX_ZERO);
        JsonNode openFda = result.path(OPENFDA_NODE_PATH);
        String manufacturerName =
                openFda.path(MANUFACTURER_NAME_NODE_PATH).get(INDEX_ZERO).asText();
        String substanceName =
                openFda.path(SUBSTANCE_NAME_NODE_PATH).get(INDEX_ZERO).asText();

        List<String> productNumbers = new ArrayList<>();
        openFda.path(PRODUCT_NDC_NODE_PATH).forEach(node -> productNumbers.add(node.asText()));

        DrugRecord drugRecord = new DrugRecord(applicationNumber, manufacturerName,
                substanceName, productNumbers);
        DrugRecord savedDrugRecord = drugRecordRepository.save(drugRecord);

        log.info("Drug record saved with application number: {}",
                savedDrugRecord.getApplicationNumber());

        return drugRecordMapper.toDto(savedDrugRecord);
    }

    @Override
    public List<DrugRecordResponseDto> getAllDrugRecords(Pageable pageable) {
        log.info("Fetching all drug records with pagination: {}", pageable);

        List<DrugRecordResponseDto> drugRecords = drugRecordRepository.findAll(pageable).stream()
                .map(drugRecordMapper::toDto)
                .toList();

        log.info("Fetched {} drug records", drugRecords.size());

        return drugRecords;
    }

    @Override
    public DrugRecordResponseDto findDrugRecordByApplicationNumber(String applicationNumber) {
        log.info("Searching for drug record with application number: {}", applicationNumber);

        return drugRecordRepository.findByApplicationNumber(applicationNumber)
                .map(drugRecordMapper::toDto)
                .orElseThrow(() -> {
                    log.error("Drug record not found for application number: {}",
                            applicationNumber);
                    return new EntityNotFoundException(NO_DRUGS_FOUND_ERROR);
                });
    }
}
