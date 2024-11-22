package com.example.loginapp.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    private final Map<String, String> userDatabase = new HashMap<>();

    public UserService() {
        userDatabase.put("admin", "admin");
        userDatabase.put("user", "pass");
        userDatabase.put("root", "root");
    }

    public boolean authenticate(String username, String password) {
        String storedPassword = userDatabase.get(username);
        return storedPassword != null && storedPassword.equals(password);
    }
}
