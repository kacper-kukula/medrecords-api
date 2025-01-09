package com.example.medrecordsapi.model;

import java.util.List;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "drug_records")
public class DrugRecord {

    @Id
    private String applicationNumber;
    private String manufacturerName;
    private String substanceName;
    private List<String> productNumbers;
}
