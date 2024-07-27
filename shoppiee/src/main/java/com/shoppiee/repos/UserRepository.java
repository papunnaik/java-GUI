package com.shoppiee.repos;


import org.springframework.data.jpa.repository.JpaRepository;

import com.shoppiee.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsernameAndPassword(String username, String password);
    User findByUsername(String username);
}