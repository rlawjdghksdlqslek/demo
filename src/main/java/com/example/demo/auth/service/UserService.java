package com.example.demo.auth.service;

import com.example.demo.auth.dto.UserRequest;
import com.example.demo.auth.entity.UserRoleType;
import com.example.demo.auth.entity.User;
import com.example.demo.auth.jwt.JWTUtil;
import com.example.demo.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JWTUtil jwtUtil;

    @Value("${default.profile.image.url}")
    private String defProfileImage;

    //회원가입
    @Transactional
    public void register(UserRequest.register register) {
        if (userRepository.findByLoginId(register.getLoginId()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 사용자 아이디입니다.");
        }

        String profileImage = register.getProfileImageUrl();
        if (register.getProfileImageUrl() == null) {
            profileImage = defProfileImage;
        }

        User user = User.builder()
                .loginId(register.getLoginId())
                .password(bCryptPasswordEncoder.encode(register.getPassword()))
                .nickname(register.getNickname())
                .name(register.getName())
                .description(register.getDescription())
                .leftFoot(register.getLeftFoot())
                .rightFoot(register.getRightFoot())
                .profileImageUrl(profileImage)
                .age(register.getAge())
                .position(register.getPosition())
                .userRoleType(UserRoleType.USER)
                .build();

        userRepository.save(user);
    }

    //로그인
    @Transactional
    public String login(UserRequest.login login) {
        //사용자 확인
        User user = userRepository.findByLoginId(login.getLoginId()).orElseThrow(()-> new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다."));

        // 비밀번호 확인
        if (!bCryptPasswordEncoder.matches(login.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        // JWT 생성 및 반환
        return jwtUtil.createJwt(user.getLoginId(), user.getUserRoleType().toString(), 60 * 60 * 10L);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }
}
