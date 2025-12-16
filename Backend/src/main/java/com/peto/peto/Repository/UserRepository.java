package com.peto.peto.Repository;

import com.peto.peto.Entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User,String> {
    // Find user by email
    User findByEmail(String email);

    // Check if email exists
    boolean existsByEmail(String email);

    // Find user by username
    User findByUsername(String username);

    // Optional: Check if username exists
    boolean existsByUsername(String username);
}
