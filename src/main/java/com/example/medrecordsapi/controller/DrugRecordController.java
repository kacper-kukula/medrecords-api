package com.example.medrecordsapi.controller;

import com.example.medrecordsapi.dto.drugrecord.DrugRecordResponseDto;
import com.example.medrecordsapi.service.DrugRecordService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Drug Records",
        description = "Operations related to drug records")
@RestController
@RequiredArgsConstructor
@RequestMapping("/drug-records")
@Slf4j
public class DrugRecordController {

    private final DrugRecordService drugRecordService;

    @Operation(summary = "Search drug records",
            description = "Search for drug records based on manufacturer and/or brand name. "
                    + "Parameter can contain part or the full name. "
                    + "Pagination is supported via the 'page' and 'size' parameters.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched drug records"),
            @ApiResponse(responseCode = "404", description = "Invalid input parameters")
    })
    @GetMapping("/search")
    public JsonNode searchDrugRecords(
            @RequestParam String manufacturerName,
            @RequestParam(required = false) String brandName,
            @RequestParam(defaultValue = "1") @Positive int page,
            @RequestParam(defaultValue = "10") @Positive int size) throws JsonProcessingException {
        log.info("Searching drug records for manufacturer: {}, brand: {}, page: {}, size: {}",
                manufacturerName, brandName, page, size);
        JsonNode response =
                drugRecordService.searchDrugRecords(manufacturerName, brandName, page, size);
        log.info("Search complete. Found {} records.", response.size());

        return response;
    }

    @Operation(summary = "Save a drug record",
            description = "Save a drug record by its application number fetched from FDA. "
                    + "Must be exact application number. "
                    + "If there is no record fetched, it will not be created.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Drug record successfully saved"),
            @ApiResponse(responseCode = "404", description = "Drug record not found")
    })
    @GetMapping("/save/{applicationNumber}")
    @ResponseStatus(HttpStatus.CREATED)
    public DrugRecordResponseDto saveDrugRecord(@PathVariable String applicationNumber)
            throws JsonProcessingException {
        log.info("Saving drug record with application number: {}", applicationNumber);
        DrugRecordResponseDto response = drugRecordService.saveDrugRecord(applicationNumber);
        log.info("Drug record with application number {} saved successfully.", applicationNumber);

        return response;
    }

    @Operation(summary = "Get all stored drug records",
            description = "Fetch all stored drug records with pagination support.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched drug records")
    })
    @GetMapping
    public List<DrugRecordResponseDto> getAllDrugRecords(Pageable pageable) {
        log.info("Fetching all drug records with pagination: page {}, size {}",
                pageable.getPageNumber(), pageable.getPageSize());
        List<DrugRecordResponseDto> response = drugRecordService.getAllDrugRecords(pageable);
        log.info("Fetched {} drug records.", response.size());

        return response;
    }

    @Operation(summary = "Find stored drug record by application number",
            description = "Fetch a stored drug record by its application number.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched drug record"),
            @ApiResponse(responseCode = "404", description = "Drug record not found")
    })
    @GetMapping("/{applicationNumber}")
    public DrugRecordResponseDto findDrugRecordByApplicationNumber(
            @PathVariable String applicationNumber) {
        log.info("Fetching drug record for application number: {}", applicationNumber);
        DrugRecordResponseDto response =
                drugRecordService.findDrugRecordByApplicationNumber(applicationNumber);
        log.info("Successfully fetched drug record for application number: {}", applicationNumber);

        return response;
    }
}
