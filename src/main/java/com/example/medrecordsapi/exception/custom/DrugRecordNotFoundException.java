package com.example.medrecordsapi.exception.custom;

public class DrugRecordNotFoundException extends RuntimeException {

    public DrugRecordNotFoundException(String message) {
        super(message);
    }
}
