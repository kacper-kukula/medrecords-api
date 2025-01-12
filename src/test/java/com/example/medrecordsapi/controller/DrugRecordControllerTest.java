package com.example.medrecordsapi.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.medrecordsapi.exception.custom.DrugRecordNotFoundException;
import com.example.medrecordsapi.model.DrugRecord;
import com.example.medrecordsapi.service.FdaApiService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WithMockUser(username = "testUser")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class DrugRecordControllerTest {

    private static final String DRUG_RECORDS_SEARCH_PATH = "/drug-records/search";
    private static final String DRUG_RECORDS_SAVE_PATH = "/drug-records/save/{applicationNumber}";
    private static final String DRUG_RECORDS_ALL_PATH = "/drug-records";
    private static final String DRUG_RECORDS_BY_APPLICATION_NUMBER_PATH =
            "/drug-records/{applicationNumber}";
    private static final String MANUFACTURER_NAME_FIELD = "openfda.manufacturer_name:";
    private static final String BRAND_NAME_FIELD = "openfda.brand_name:";
    private static final String APPLICATION_NUMBER_FIELD = "openfda.application_number:";
    private static final String AND_OPERATOR = "+AND+";
    private static final int PAGE_ONE = 1;
    private static final int SIZE_TEN = 10;
    private static final String TEST_MANUFACTURER = "Test Manufacturer";
    private static final String TEST_BRAND_NAME = "Test Brand";
    private static final String TEST_APPLICATION_NUMBER = "123456";
    private static final String MOCK_API_RESPONSE = """
            {
              "results": [
                {
                  "openfda": {
                    "manufacturer_name": ["Test Manufacturer"],
                    "brand_name": ["Test Brand"],
                    "substance_name": ["DOSTARLIMAB"],
                    "application_number": ["123456"],
                    "product_ndc": ["63539-183", "63539-242"]
                  }
                }
              ]
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FdaApiService fdaApiService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void setup() {
        mongoTemplate.getDb().drop();
    }

    @Nested
    @DisplayName("Search Drug Records Tests")
    class SearchDrugRecordsTests {

        @Test
        @DisplayName("Search drug records by manufacturer name")
        void searchDrugRecords_ValidManufacturerName_ReturnsDrugRecords() throws Exception {
            String searchQuery = MANUFACTURER_NAME_FIELD + TEST_MANUFACTURER;
            when(fdaApiService.fetchDrugData("", searchQuery, PAGE_ONE, SIZE_TEN))
                    .thenReturn(MOCK_API_RESPONSE);

            mockMvc.perform(get(DRUG_RECORDS_SEARCH_PATH)
                            .param("manufacturerName", TEST_MANUFACTURER)
                            .param("page", "1")
                            .param("size", "10")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()").value(1))
                    .andExpect(jsonPath("$.results[0].openfda.manufacturer_name[0]")
                            .value(TEST_MANUFACTURER));
        }

        @Test
        @DisplayName("Search drug records by manufacturer and brand name")
        void searchDrugRecords_ValidManufacturerAndBrandName_ReturnsDrugRecords() throws Exception {
            String searchQuery = MANUFACTURER_NAME_FIELD + TEST_MANUFACTURER + AND_OPERATOR
                    + BRAND_NAME_FIELD + TEST_BRAND_NAME;
            when(fdaApiService.fetchDrugData("", searchQuery, PAGE_ONE, SIZE_TEN))
                    .thenReturn(MOCK_API_RESPONSE);

            mockMvc.perform(get(DRUG_RECORDS_SEARCH_PATH)
                            .param("manufacturerName", TEST_MANUFACTURER)
                            .param("brandName", TEST_BRAND_NAME)
                            .param("page", "1")
                            .param("size", "10")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()").value(1))
                    .andExpect(jsonPath("$.results[0].openfda.manufacturer_name[0]")
                            .value(TEST_MANUFACTURER))
                    .andExpect(jsonPath("$.results[0].openfda.brand_name[0]")
                            .value(TEST_BRAND_NAME));
        }

        @Test
        @DisplayName("Search drug records with missing manufacturer name")
        void searchDrugRecords_MissingManufacturerName_ReturnsBadRequest() throws Exception {
            mockMvc.perform(get(DRUG_RECORDS_SEARCH_PATH)
                            .param("page", "1")
                            .param("size", "10")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Save Drug Record Tests")
    class SaveDrugRecordTests {

        @Test
        @DisplayName("Save drug record successfully")
        void saveDrugRecord_ValidApplicationNumber_ReturnsDrugRecordResponseDto() throws Exception {
            when(fdaApiService.fetchDrugData(APPLICATION_NUMBER_FIELD, TEST_APPLICATION_NUMBER,
                    PAGE_ONE, PAGE_ONE)).thenReturn(MOCK_API_RESPONSE);

            mockMvc.perform(get(DRUG_RECORDS_SAVE_PATH, TEST_APPLICATION_NUMBER)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.applicationNumber").value(TEST_APPLICATION_NUMBER));
        }

        @Test
        @DisplayName("Save drug record with invalid application number")
        void saveDrugRecord_InvalidApplicationNumber_ReturnsNotFound() throws Exception {
            when(fdaApiService.fetchDrugData(APPLICATION_NUMBER_FIELD, TEST_APPLICATION_NUMBER,
                    PAGE_ONE, PAGE_ONE))
                    .thenThrow(new DrugRecordNotFoundException("Drug record not found"));

            mockMvc.perform(get(DRUG_RECORDS_SAVE_PATH, TEST_APPLICATION_NUMBER)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errors[0]").exists());
        }
    }

    @Nested
    @DisplayName("Get All Drug Records Tests")
    class GetAllDrugRecordsTests {

        @Test
        @DisplayName("Get all drug records from database")
        void getAllDrugRecords_RecordsExistInDb_ReturnsDrugRecordList() throws Exception {
            DrugRecord drugRecord1 = new DrugRecord("12345", "Pfizer",
                    "Paracetamol", List.of("123", "321", "456"));
            DrugRecord drugRecord2 = new DrugRecord("54321", "Pfizer",
                    "Paracetamol", List.of("444", "666", "555"));
            mongoTemplate.save(drugRecord1);
            mongoTemplate.save(drugRecord2);

            mockMvc.perform(get(DRUG_RECORDS_ALL_PATH)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()").value(2));
        }
    }

    @Nested
    @DisplayName("Find Drug Record by Application Number Tests")
    class FindDrugRecordByApplicationNumberTests {

        @Test
        @DisplayName("Find drug record by application number")
        void findDrugRecordByApplicationNumber_ValidNumber_ReturnsDrugRecordResponseDto()
                throws Exception {
            DrugRecord drugRecord = new DrugRecord(TEST_APPLICATION_NUMBER, "Pfizer",
                    "Paracetamol", List.of("123", "321", "456"));
            mongoTemplate.save(drugRecord);

            mockMvc.perform(get(DRUG_RECORDS_BY_APPLICATION_NUMBER_PATH, TEST_APPLICATION_NUMBER)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.applicationNumber").value(TEST_APPLICATION_NUMBER));
        }

        @Test
        @DisplayName("Find drug record by non-existing application number")
        void findDrugRecordByApplicationNumber_NonExisting_ReturnsNotFound() throws Exception {
            DrugRecord drugRecord = new DrugRecord("000", "Pfizer",
                    "Paracetamol", List.of("123", "321", "456"));
            mongoTemplate.save(drugRecord);

            mockMvc.perform(get(DRUG_RECORDS_BY_APPLICATION_NUMBER_PATH, TEST_APPLICATION_NUMBER)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errors[0]").exists());
        }
    }
}
