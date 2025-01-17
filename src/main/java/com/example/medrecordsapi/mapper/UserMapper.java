package com.example.medrecordsapi.mapper;

import com.example.medrecordsapi.config.MapperConfig;
import com.example.medrecordsapi.dto.user.UserRegistrationRequestDto;
import com.example.medrecordsapi.dto.user.UserResponseDto;
import com.example.medrecordsapi.model.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {

    UserResponseDto toDto(User user);

    User toModel(UserRegistrationRequestDto requestDto);
}
