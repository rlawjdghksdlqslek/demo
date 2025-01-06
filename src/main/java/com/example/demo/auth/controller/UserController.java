package com.example.demo.auth.controller;

import com.example.demo.auth.dto.CustomUserDetails;
import com.example.demo.auth.dto.UserRequest;
import com.example.demo.auth.entity.User;
import com.example.demo.auth.repository.UserRepository;
import com.example.demo.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;

    //회원가입
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRequest.register register) {
        userService.register(register);
        return ResponseEntity.ok("회원가입에 성공하였습니다.");
    }

    //로그인
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserRequest.login login) {
        String token = userService.login(login);
        return ResponseEntity.ok("Bearer " + token);
    }

    //프로필 조회
    @GetMapping("/profile")
    public ResponseEntity<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        User user = userRepository.findByLoginId(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return ResponseEntity.ok(user);
    }

    //프로필 수정
    @PutMapping("/profile")
    public ResponseEntity<String> updateProfile(@RequestBody UserRequest.UpdateProfile updateProfile) {
        userService.updateProfile(updateProfile);
        return ResponseEntity.ok("프로필이 성공적으로 수정되었습니다.");
    }

    //비밀번호 변경
    @PutMapping("/profile/password")
    public ResponseEntity<String> changePassword(@RequestBody UserRequest.ChangePassword changePassword) {
        userService.changePassword(changePassword);
        return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
    }

    //회원 탈퇴
    @DeleteMapping("/withdrawal")
    public ResponseEntity<String> deleteAccount(Authentication authentication) {
        userService.deleteAccount(authentication);
        return ResponseEntity.ok("계정이 성공적으로 삭제되었습니다.");
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