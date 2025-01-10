package com.example.medrecordsapi.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@EqualsAndHashCode
@Document(collection = "drug_records")
public class DrugRecord {

    @Id
    private String applicationNumber;
    private String manufacturerName;
    private String substanceName;
    private List<String> productNumbers;
}
