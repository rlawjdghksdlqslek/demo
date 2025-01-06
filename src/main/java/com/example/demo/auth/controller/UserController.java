package com.example.demo.auth.controller;

import com.example.demo.auth.dto.UserRequest;
import com.example.demo.auth.entity.User;
import com.example.demo.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRequest.register register) {
        userService.register(register);
        return ResponseEntity.ok("회원가입에 성공하였습니다.");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserRequest.login login) {
        String token = userService.login(login);
        return ResponseEntity.ok("Bearer " + token);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }
}