package com.example.medrecordsapi.controller;

import com.example.medrecordsapi.dto.drugrecord.DrugRecordResponseDto;
import com.example.medrecordsapi.service.DrugRecordService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/drug-records")
@RequiredArgsConstructor
public class DrugRecordController {

    private final DrugRecordService drugRecordService;

    @GetMapping("/search")
    public JsonNode searchDrugRecords(
            @RequestParam String manufacturerName,
            @RequestParam(required = false) String brandName,
            @RequestParam(defaultValue = "1") @Positive int page,
            @RequestParam(defaultValue = "10") @Positive int size) throws JsonProcessingException {
        return drugRecordService.searchDrugRecords(manufacturerName, brandName, page, size);
    }

    @GetMapping("/save/{applicationNumber}")
    @ResponseStatus(HttpStatus.CREATED)
    public DrugRecordResponseDto saveDrugRecord(@PathVariable String applicationNumber)
            throws JsonProcessingException {
        return drugRecordService.saveDrugRecord(applicationNumber);
    }

    @GetMapping
    public List<DrugRecordResponseDto> getAllDrugRecords(Pageable pageable) {
        return drugRecordService.getAllDrugRecords(pageable);
    }

    @GetMapping("/{applicationNumber}")
    public DrugRecordResponseDto findDrugRecordByApplicationNumber(
            @PathVariable String applicationNumber) {
        return drugRecordService.findDrugRecordByApplicationNumber(applicationNumber);
    }
}
