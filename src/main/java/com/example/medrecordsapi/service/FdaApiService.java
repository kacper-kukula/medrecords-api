package com.example.medrecordsapi.service;

public interface FdaApiService {

    String fetchDrugData(String queryParam, String queryValue, int page, int size);
}
