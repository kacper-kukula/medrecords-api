package com.example.medrecordsapi.service;

import com.example.medrecordsapi.model.DrugRecord;
import org.springframework.data.domain.Pageable;

public interface DrugRecordService {

    void saveDrugRecord(DrugRecord drugRecord);

    void findByApplicationNumber(String applicationNumber);

    void findAllDrugRecords();

    void findByManufacturerName(String manufacturerName, Pageable pageable);

    void findBySubstanceName(String substanceName, Pageable pageable);

    void findByProductNumbersContaining(String productNumber, Pageable pageable);
}
