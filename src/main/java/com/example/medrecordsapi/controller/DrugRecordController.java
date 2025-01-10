package com.example.medrecordsapi.controller;

import com.example.medrecordsapi.model.DrugRecord;
import com.example.medrecordsapi.service.DrugRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/drug-records")
@RequiredArgsConstructor
public class DrugRecordController {

    private final DrugRecordService drugRecordService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DrugRecord saveDrugRecord(@RequestBody DrugRecord drugRecord) {
        return drugRecordService.saveDrugRecord(drugRecord);
    }

    @GetMapping("/{applicationNumber}")
    public DrugRecord getDrugRecordByApplicationNumber(@PathVariable String applicationNumber) {
        return drugRecordService.findByApplicationNumber(applicationNumber);
    }

    @GetMapping("/manufacturer")
    public Page<DrugRecord> getDrugRecordsByManufacturerName(
            @RequestParam String manufacturerName, Pageable pageable) {
        return drugRecordService.findAllByManufacturerName(manufacturerName, pageable);
    }

    @GetMapping("/substance")
    public Page<DrugRecord> getDrugRecordsBySubstanceName(
            @RequestParam String substanceName, Pageable pageable) {
        return drugRecordService.findAllBySubstanceName(substanceName, pageable);
    }

    @GetMapping("/product-number")
    public Page<DrugRecord> getDrugRecordsByProductNumbersContaining(
            @RequestParam String productNumber, Pageable pageable) {
        return drugRecordService.findAllByProductNumbersContaining(productNumber, pageable);
    }

    @GetMapping
    public Page<DrugRecord> getAllDrugRecords(Pageable pageable) {
        return drugRecordService.findAllDrugRecords(pageable);
    }
}