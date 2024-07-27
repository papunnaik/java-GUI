package com.shoppiee.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.shoppiee.model.User;
import com.shoppiee.service.UserService;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public boolean login(@RequestBody User user) {
        return userService.authenticateUser(user.getUsername(), user.getPassword());
    }

    @PostMapping("/createAccount")
    public void createAccount(@RequestBody User user) {
        userService.createUser(user);
    }

    @GetMapping("/getUserType/{username}")
    public String getUserType(@PathVariable String username) {
        return userService.getUserType(username);
    }
}