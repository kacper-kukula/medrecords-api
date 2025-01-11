package com.example.medrecordsapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @InjectMocks
    private DrugRecordServiceImpl drugRecordService;

    @Test
    @DisplayName("Test searchDrugRecords method")
    public void searchDrugRecords_ValidParameters_ReturnsJsonNode() throws Exception {
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
    @DisplayName("Test saving a drug record")
    public void saveDrugRecord_ValidDrugRecord_ReturnsSavedDrugRecord() {
        DrugRecord validDrugRecord = new DrugRecord("12345", "Greenfield Laboratories",
                "Aspirin", List.of("PN12345", "PN12346", "PN12347"));
        when(drugRecordRepository.save(validDrugRecord)).thenReturn(validDrugRecord);

        DrugRecord actual = drugRecordService.saveDrugRecord(validDrugRecord);

        verify(drugRecordRepository, times(1)).save(validDrugRecord);
        assertThat(actual).isEqualTo(validDrugRecord);
    }

    @Test
    @DisplayName("Test finding a drug record by application number")
    public void findByApplicationNumber_ValidApplicationNumber_ReturnsDrugRecord() {
        String applicationNumber = "67890";
        DrugRecord validDrugRecord = new DrugRecord(applicationNumber, "Sunridge Pharma",
                "Paracetamol", List.of("PN67890", "PN67891", "PN67892"));
        when(drugRecordRepository.findByApplicationNumber(applicationNumber))
                .thenReturn(Optional.of(validDrugRecord));

        DrugRecord actual = drugRecordService.findByApplicationNumber(applicationNumber);

        verify(drugRecordRepository, times(1)).findByApplicationNumber(applicationNumber);
        assertThat(actual).isEqualTo(validDrugRecord);
    }

    @Test
    @DisplayName("Test finding all drug records by manufacturer name")
    public void findAllByManufacturerName_ValidManufacturerName_ReturnsDrugRecords() {
        String manufacturerName = "BlueMountain Meds";
        DrugRecord validDrugRecord = new DrugRecord("11223", manufacturerName,
                "Ibuprofen", List.of("PN11223", "PN11224", "PN11225"));
        Pageable pageable = Pageable.unpaged();
        Page<DrugRecord> drugRecordPage = new PageImpl<>(List.of(validDrugRecord));
        when(drugRecordRepository.findByManufacturerName(manufacturerName, pageable))
                .thenReturn(drugRecordPage);

        Page<DrugRecord> actual =
                drugRecordService.findAllByManufacturerName(manufacturerName, pageable);

        verify(drugRecordRepository, times(1)).findByManufacturerName(manufacturerName, pageable);
        assertThat(actual).isNotEmpty();
        assertThat(actual.getContent().get(0)).isEqualTo(validDrugRecord);
    }

    @Test
    @DisplayName("Test finding all drug records by substance name")
    public void findAllBySubstanceName_ValidSubstanceName_ReturnsDrugRecords() {
        String substanceName = "Amoxicillin";
        DrugRecord validDrugRecord = new DrugRecord("22456", "Oakwood Biotech",
                substanceName, List.of("PN22456", "PN22457", "PN22458"));
        Pageable pageable = Pageable.unpaged();
        Page<DrugRecord> drugRecordPage = new PageImpl<>(List.of(validDrugRecord));
        when(drugRecordRepository.findBySubstanceName(substanceName, pageable))
                .thenReturn(drugRecordPage);

        Page<DrugRecord> actual = drugRecordService.findAllBySubstanceName(substanceName, pageable);

        verify(drugRecordRepository, times(1)).findBySubstanceName(substanceName, pageable);
        assertThat(actual).isNotEmpty();
        assertThat(actual.getContent().get(0)).isEqualTo(validDrugRecord);
    }

    @Test
    @DisplayName("Test finding all drug records by product numbers containing specific number")
    public void findAllByProductNumbersContaining_ValidProductNumber_ReturnsDrugRecords() {
        String productNumber = "PN33469";
        DrugRecord validDrugRecord = new DrugRecord("33467", "Silverline Therapeutics",
                "Metformin", List.of("PN33467", "PN33468", productNumber));
        Pageable pageable = Pageable.unpaged();
        Page<DrugRecord> drugRecordPage = new PageImpl<>(List.of(validDrugRecord));
        when(drugRecordRepository.findByProductNumbersContaining(productNumber, pageable))
                .thenReturn(drugRecordPage);

        Page<DrugRecord> actual =
                drugRecordService.findAllByProductNumbersContaining(productNumber, pageable);

        verify(drugRecordRepository, times(1))
                .findByProductNumbersContaining(productNumber, pageable);
        assertThat(actual).isNotEmpty();
        assertThat(actual.getContent().get(0)).isEqualTo(validDrugRecord);
    }

    @Test
    @DisplayName("Test finding all drug records")
    public void findAll_ReturnsAllDrugRecords() {
        DrugRecord validDrugRecord1 = new DrugRecord("44578", "Redwood Health Co.",
                "Atorvastatin", List.of("PN44578", "PN44579", "PN44580"));
        DrugRecord validDrugRecord2 = new DrugRecord("55689", "CrystalClear Pharma",
                "Omeprazole", List.of("PN55689", "PN55690", "PN55691"));
        Pageable pageable = Pageable.unpaged();
        Page<DrugRecord> drugRecordPage =
                new PageImpl<>(List.of(validDrugRecord1, validDrugRecord2));
        when(drugRecordRepository.findAll(pageable)).thenReturn(drugRecordPage);

        Page<DrugRecord> actual = drugRecordService.findAllDrugRecords(pageable);

        verify(drugRecordRepository, times(1))
                .findAll(pageable);
        assertThat(actual).isNotEmpty();
        assertThat(actual.getContent()).containsExactly(validDrugRecord1, validDrugRecord2);
    }
}
