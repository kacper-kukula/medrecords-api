package com.example.medrecordsapi.dto.drugrecord;

import java.util.List;

public record DrugRecordResponseDto(
        String applicationNumber,
        String manufacturerName,
        String substanceName,
        List<String> productNumbers
) {
}
