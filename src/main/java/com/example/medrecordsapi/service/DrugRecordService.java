package com.example.medrecordsapi.service;

import com.example.medrecordsapi.dto.drugrecord.DrugRecordResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface DrugRecordService {

    JsonNode searchDrugRecords(String manufacturerName, String brandName, int page, int size)
            throws JsonProcessingException;

    DrugRecordResponseDto saveDrugRecord(String applicationNumber) throws JsonProcessingException;

    List<DrugRecordResponseDto> getAllDrugRecords(Pageable pageable);

    DrugRecordResponseDto findDrugRecordByApplicationNumber(String applicationNumber);
}
