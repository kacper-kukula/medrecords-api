package com.example.medrecordsapi.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.medrecordsapi.model.DrugRecord;
import com.example.medrecordsapi.repository.DrugRecordRepository;
import com.example.medrecordsapi.service.impl.DrugRecordServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class DrugRecordServiceTest {

    @Mock
    private DrugRecordRepository drugRecordRepository;

    @InjectMocks
    private DrugRecordServiceImpl drugRecordService;

    private DrugRecord drugRecord1;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        drugRecord1 = new DrugRecord();
        drugRecord1.setApplicationNumber("12345");
        drugRecord1.setManufacturerName("Acme Pharmaceuticals");
    }

    @Test
    public void testSaveDrugRecord() {
        drugRecordService.saveDrugRecord(drugRecord1);

        verify(drugRecordRepository, times(1)).save(drugRecord1);
    }

    @Test
    public void testFindByApplicationNumber() {
        String applicationNumber = "12345";

        drugRecordService.findByApplicationNumber(applicationNumber);

        verify(drugRecordRepository, times(1)).findByApplicationNumber(applicationNumber);
    }

    @Test
    public void testFindByManufacturerName() {
        String manufacturerName = "Beta Pharmaceuticals";
        Pageable pageable = Pageable.unpaged();

        drugRecordService.findByManufacturerName(manufacturerName, pageable);

        verify(drugRecordRepository, times(1)).findByManufacturerName(manufacturerName, pageable);
    }

    @Test
    public void testFindBySubstanceName() {
        String substanceName = "Aspirin";
        Pageable pageable = Pageable.unpaged();

        drugRecordService.findBySubstanceName(substanceName, pageable);

        verify(drugRecordRepository, times(1)).findBySubstanceName(substanceName, pageable);
    }

    @Test
    public void testFindByProductNumbersContaining() {
        String productNumber = "PN12345";
        Pageable pageable = Pageable.unpaged();

        drugRecordService.findByProductNumbersContaining(productNumber, pageable);

        verify(drugRecordRepository, times(1))
                .findByProductNumbersContaining(productNumber, pageable);
    }

    @Test
    public void testFindAllDrugRecords() {
        drugRecordService.findAllDrugRecords();

        verify(drugRecordRepository, times(1)).findAll();
    }
}
