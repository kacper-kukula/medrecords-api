package com.example.medrecordsapi.service.impl;

import com.example.medrecordsapi.model.DrugRecord;
import com.example.medrecordsapi.repository.DrugRecordRepository;
import com.example.medrecordsapi.service.DrugRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DrugRecordServiceImpl implements DrugRecordService {

    private DrugRecordRepository drugRecordRepository;

    @Override
    public void saveDrugRecord(DrugRecord drugRecord) {
        drugRecordRepository.save(drugRecord);
    }

    @Override
    public void findByApplicationNumber(String applicationNumber) {
        drugRecordRepository.findByApplicationNumber(applicationNumber);
    }

    @Override
    public void findAllDrugRecords() {
        drugRecordRepository.findAll();
    }

    @Override
    public void findByManufacturerName(String manufacturerName, Pageable pageable) {
        drugRecordRepository.findByManufacturerName(manufacturerName, pageable);
    }

    @Override
    public void findBySubstanceName(String substanceName, Pageable pageable) {
        drugRecordRepository.findBySubstanceName(substanceName, pageable);
    }

    @Override
    public void findByProductNumbersContaining(String productNumber, Pageable pageable) {
        drugRecordRepository.findByProductNumbersContaining(productNumber, pageable);

    }
}
