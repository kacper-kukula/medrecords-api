package com.example.medrecordsapi.repository;

import com.example.medrecordsapi.model.DrugRecord;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DrugRecordRepository extends MongoRepository<DrugRecord, String> {

    Optional<DrugRecord> findByApplicationNumber(String applicationNumber);

    Page<DrugRecord> findByManufacturerName(String manufacturerName, Pageable pageable);

    Page<DrugRecord> findBySubstanceName(String substanceName, Pageable pageable);

    Page<DrugRecord> findByProductNumbersContaining(String productNumber, Pageable pageable);
}
