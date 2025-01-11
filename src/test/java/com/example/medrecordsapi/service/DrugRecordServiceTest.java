package com.example.medrecordsapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.medrecordsapi.dto.drugrecord.DrugRecordResponseDto;
import com.example.medrecordsapi.mapper.DrugRecordMapper;
import com.example.medrecordsapi.model.DrugRecord;
import com.example.medrecordsapi.repository.DrugRecordRepository;
import com.example.medrecordsapi.service.impl.DrugRecordServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
public class DrugRecordServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private DrugRecordRepository drugRecordRepository;

    @Mock
    private DrugRecordMapper drugRecordMapper;

    @InjectMocks
    private DrugRecordServiceImpl drugRecordService;

    @Test
    @DisplayName("Test searchDrugRecords method")
    void searchDrugRecords_ValidParameters_ReturnsJsonNode() throws Exception {
        String manufacturerName = "Greenfield Laboratories";
        String brandName = "Aspirin";
        int page = 1;
        int size = 10;
        String mockJsonResponse = "{\"results\": [{\"application_number\": "
                + "\"12345\", \"brand_name\": \"Avastin\"}]}";
        JsonNode mockJsonNode = new ObjectMapper().readTree(mockJsonResponse);
        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenReturn(mockJsonResponse);
        when(objectMapper.readTree(mockJsonResponse))
                .thenReturn(mockJsonNode);

        JsonNode actualJsonNode =
                drugRecordService.searchDrugRecords(manufacturerName, brandName, page, size);

        verify(restTemplate, times(1)).getForObject(anyString(), eq(String.class));
        assertThat(actualJsonNode).isNotNull();
        assertThat(actualJsonNode.get("results").size()).isGreaterThan(0);
        assertThat(actualJsonNode.get("results").get(0).get("application_number").asText())
                .isEqualTo("12345");
    }

    @Test
    @DisplayName("Test saveDrugRecord - Success")
    void saveDrugRecord_ValidApplicationNumber_SavesAndReturnsDrugRecord() throws Exception {
        String applicationNumber = "12345";
        String mockJsonResponse = """
                {
                    "results": [
                        {
                            "openfda": {
                                "manufacturer_name": ["Test Manufacturer"],
                                "substance_name": ["Test Substance"],
                                "product_ndc": ["12345-6789", "98765-4321"]
                            }
                        }
                    ]
                }
                """;
        JsonNode mockJsonNode = new ObjectMapper().readTree(mockJsonResponse);
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(mockJsonResponse);
        when(objectMapper.readTree(mockJsonResponse)).thenReturn(mockJsonNode);
        DrugRecord mockDrugRecord = new DrugRecord(applicationNumber, "Test Manufacturer",
                "Test Substance", List.of("12345-6789", "98765-4321"));
        when(drugRecordRepository.save(any(DrugRecord.class))).thenReturn(mockDrugRecord);
        DrugRecordResponseDto mockResponseDto = new DrugRecordResponseDto(
                applicationNumber, "Test Manufacturer",
                "Test Substance", List.of("12345-6789", "98765-4321"));
        when(drugRecordMapper.toDto(mockDrugRecord)).thenReturn(mockResponseDto);

        DrugRecordResponseDto result = drugRecordService.saveDrugRecord(applicationNumber);

        assertThat(result).isNotNull();
        assertThat(result.applicationNumber()).isEqualTo(applicationNumber);
        assertThat(result.manufacturerName()).isEqualTo("Test Manufacturer");
        assertThat(result.substanceName()).isEqualTo("Test Substance");
        assertThat(result.productNumbers()).containsExactly("12345-6789", "98765-4321");

        verify(restTemplate, times(1)).getForObject(anyString(), eq(String.class));
        verify(drugRecordRepository, times(1)).save(any(DrugRecord.class));
        verify(drugRecordMapper, times(1)).toDto(mockDrugRecord);
    }

    @Test
    @DisplayName("Test finding a drug record by application number")
    void findDrugRecordByApplicationNumber_ValidApplicationNumber_ReturnsDrugRecord() {
        String applicationNumber = "67890";
        DrugRecord drugRecord = new DrugRecord(applicationNumber, "Sunridge Pharma",
                "Paracetamol", List.of("PN67890", "PN67891", "PN67892"));
        DrugRecordResponseDto expected =
                new DrugRecordResponseDto(drugRecord.getApplicationNumber(),
                        drugRecord.getManufacturerName(), drugRecord.getSubstanceName(),
                        drugRecord.getProductNumbers());
        when(drugRecordRepository.findByApplicationNumber(applicationNumber))
                .thenReturn(Optional.of(drugRecord));
        when(drugRecordMapper.toDto(drugRecord)).thenReturn(expected);

        DrugRecordResponseDto actual =
                drugRecordService.findDrugRecordByApplicationNumber(applicationNumber);

        verify(drugRecordRepository, times(1)).findByApplicationNumber(applicationNumber);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Test finding all drug records")
    void findAll_ReturnsAllDrugRecords() {
        DrugRecord validDrugRecord1 = new DrugRecord("44578", "Redwood Health Co.",
                "Atorvastatin", List.of("PN44578", "PN44579", "PN44580"));
        DrugRecord validDrugRecord2 = new DrugRecord("55689", "CrystalClear Pharma",
                "Omeprazole", List.of("PN55689", "PN55690", "PN55691"));
        DrugRecordResponseDto expectedDto1 = new DrugRecordResponseDto("44578",
                "Redwood Health Co.", "Atorvastatin", List.of("PN44578", "PN44579", "PN44580"));
        DrugRecordResponseDto expectedDto2 = new DrugRecordResponseDto("55689",
                "CrystalClear Pharma", "Omeprazole", List.of("PN55689", "PN55690", "PN55691"));
        Pageable pageable = Pageable.unpaged();
        Page<DrugRecord> drugRecordPage =
                new PageImpl<>(List.of(validDrugRecord1, validDrugRecord2));
        when(drugRecordRepository.findAll(pageable)).thenReturn(drugRecordPage);
        when(drugRecordMapper.toDto(validDrugRecord1)).thenReturn(expectedDto1);
        when(drugRecordMapper.toDto(validDrugRecord2)).thenReturn(expectedDto2);

        List<DrugRecordResponseDto> actual = drugRecordService.getAllDrugRecords(pageable);

        verify(drugRecordRepository, times(1))
                .findAll(pageable);
        assertThat(actual).isNotEmpty();
        assertThat(actual).containsExactly(expectedDto1, expectedDto2);
    }
}
