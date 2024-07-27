package com.shoppiee.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shoppiee.model.User;
import com.shoppiee.repos.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public boolean authenticateUser(String username, String password) {
        return userRepository.findByUsernameAndPassword(username, password) != null;
    }

    public void createUser(User user) {
        userRepository.save(user);
    }

    public String getUserType(String username) {
        User user = userRepository.findByUsername(username);
        return user != null ? user.getPosition() : "";
    }
}