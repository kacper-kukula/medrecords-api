package com.example.medrecordsapi.service.impl;

import com.example.medrecordsapi.model.DrugRecord;
import com.example.medrecordsapi.repository.DrugRecordRepository;
import com.example.medrecordsapi.service.DrugRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DrugRecordServiceImpl implements DrugRecordService {

    private DrugRecordRepository drugRecordRepository;

    @Override
    public DrugRecord saveDrugRecord(DrugRecord drugRecord) {
        DrugRecord savedDrugRecord = drugRecordRepository.save(drugRecord);

        return savedDrugRecord;

    }

    @Override
    public DrugRecord findByApplicationNumber(String applicationNumber) {
        return drugRecordRepository.findByApplicationNumber(applicationNumber)
                .orElseThrow(() -> new RuntimeException("Not Found"));
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
}
