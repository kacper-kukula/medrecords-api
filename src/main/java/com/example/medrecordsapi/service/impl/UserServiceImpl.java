package com.example.medrecordsapi.service.impl;

import com.example.medrecordsapi.model.User;
import com.example.medrecordsapi.repository.UserRepository;
import com.example.medrecordsapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    @Override
    public void register(User user) {
        userRepository.save(user);
    }
}
