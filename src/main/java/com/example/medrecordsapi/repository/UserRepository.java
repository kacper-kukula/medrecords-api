package com.example.medrecordsapi.repository;

import com.example.medrecordsapi.model.User;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmailAndIsDeletedFalse(String email);
}