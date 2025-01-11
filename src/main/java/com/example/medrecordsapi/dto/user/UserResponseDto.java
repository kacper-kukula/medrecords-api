package com.example.medrecordsapi.dto.user;

import com.example.medrecordsapi.model.Role;

public record UserResponseDto(
        String id,
        String email,
        String firstName,
        String lastName,
        Role role
) {
}
