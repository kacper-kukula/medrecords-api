package com.example.medrecordsapi.security;

import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    public String authenticate() {
        return "JWT Token";
    }
}
