package com.example.medrecordsapi.mapper;

import com.example.medrecordsapi.config.MapperConfig;
import com.example.medrecordsapi.dto.drugrecord.DrugRecordResponseDto;
import com.example.medrecordsapi.model.DrugRecord;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface DrugRecordMapper {

    DrugRecordResponseDto toDto(DrugRecord drugRecord);
}
