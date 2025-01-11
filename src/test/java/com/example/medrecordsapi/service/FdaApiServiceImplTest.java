package com.example.medrecordsapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.example.medrecordsapi.exception.custom.DrugRecordNotFoundException;
import com.example.medrecordsapi.service.impl.FdaApiServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
public class FdaApiServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private FdaApiServiceImpl fdaApiServiceImpl;

    @Test
    @DisplayName("Valid response returned successfully")
    void fetchDrugData_ValidResponse_ReturnsJsonString() {
        String manufacturerName = "Greenfield Laboratories";
        String brandName = "Aspirin";
        int page = 1;
        int size = 10;
        String mockJsonResponse = "{\"results\": [{\"application_number\": \"12345\","
                + " \"brand_name\": \"Avastin\"}]}";
        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenReturn(mockJsonResponse);

        String result = fdaApiServiceImpl.fetchDrugData(manufacturerName, brandName, page, size);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(mockJsonResponse);
    }

    @Test
    @DisplayName("Invalid response throws exception")
    void fetchDrugData_InvalidResponse_ThrowsException() {
        String manufacturerName = "Nonexistent Manufacturer";
        String brandName = "Nonexistent Brand";
        int page = 1;
        int size = 10;
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenThrow(
                new RestClientResponseException("Not Found", 404, "Not Found", null, null, null));

        assertThatThrownBy(
                () -> fdaApiServiceImpl.fetchDrugData(manufacturerName, brandName, page, size))
                .isInstanceOf(DrugRecordNotFoundException.class)
                .hasMessageContaining("No drug records found");
    }

    @Test
    @DisplayName("Null or empty API key does not break the API call")
    void fetchDrugData_NoApiKey_DoesNotBreakRequest() {
        String manufacturerName = "Greenfield Laboratories";
        String brandName = "Aspirin";
        int page = 1;
        int size = 10;
        String mockJsonResponse = "{\"results\": [{\"application_number\": \"12345\","
                + " \"brand_name\": \"Avastin\"}]}";
        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenReturn(mockJsonResponse);
        System.setProperty("FDA_API_KEY", "");

        String result = fdaApiServiceImpl.fetchDrugData(manufacturerName, brandName, page, size);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(mockJsonResponse);
    }
}
