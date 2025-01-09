package com.example.medrecordsapi.service;

import com.example.medrecordsapi.model.DrugRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DrugRecordService {

    DrugRecord saveDrugRecord(DrugRecord drugRecord);

    DrugRecord findByApplicationNumber(String applicationNumber);

    Page<DrugRecord> findAllByManufacturerName(String manufacturerName, Pageable pageable);

    Page<DrugRecord> findAllBySubstanceName(String substanceName, Pageable pageable);

    Page<DrugRecord> findAllByProductNumbersContaining(String productNumber, Pageable pageable);

    Page<DrugRecord> findAllDrugRecords(Pageable pageable);
}
