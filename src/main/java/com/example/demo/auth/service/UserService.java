package com.example.demo.auth.service;

import com.example.demo.auth.dto.CustomUserDetails;
import com.example.demo.auth.dto.UserRequest;
import com.example.demo.auth.dto.UserRoleType;
import com.example.demo.auth.entity.User;
import com.example.demo.auth.jwt.JWTUtil;
import com.example.demo.auth.repository.UserRepository;
import com.example.demo.team.entity.Team;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JWTUtil jwtUtil;
    private final TokenService tokenService;

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
        User user = userRepository.findByLoginId(login.getLoginId()).orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다."));

        // 비밀번호 확인
        if (!bCryptPasswordEncoder.matches(login.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        // JWT 생성 및 반환
        return jwtUtil.createJwt(user.getLoginId(), user.getUserRoleType().toString(), 60 * 60 * 1000L);
    }

    //로그아웃
    public void logout(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");

        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);

            // 토큰 만료 시간 계산
            long expiryMs = jwtUtil.getExpiryTime(token) - System.currentTimeMillis();

            if (expiryMs > 0) {
                // 블랙리스트에 추가
                tokenService.addToBlackList(token, expiryMs);
            } else {
                throw new IllegalArgumentException("이미 만료된 토큰입니다.");
            }
        } else {
            throw new IllegalArgumentException("Authorization 헤더가 없습니다.");
        }
    }

    //프로필 수정
    @Transactional
    public void updateProfile(UserRequest.UpdateProfile updateProfile) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        User user = userRepository.findByLoginId(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        user.setName(updateProfile.getName());
        user.setNickname(updateProfile.getNickname());
        user.setProfileImageUrl(updateProfile.getProfileImageUrl());
        user.setAge(updateProfile.getAge());
        user.setPosition(updateProfile.getPosition());
        user.setDescription(updateProfile.getDescription());
        user.setLeftFoot(updateProfile.getLeftFoot());
        user.setRightFoot(updateProfile.getRightFoot());

        userRepository.save(user);
    }

    //비밀번호 변경
    @Transactional
    public void changePassword(UserRequest.ChangePassword changePassword) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        User user = userRepository.findByLoginId(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (!bCryptPasswordEncoder.matches(changePassword.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 올바르지 않습니다.");
        }

        user.setPassword(bCryptPasswordEncoder.encode(changePassword.getNewPassword()));
        userRepository.save(user);
    }

    //회원 탈퇴
    @Transactional
    public void deleteAccount(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userRepository.findByLoginId(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        userRepository.delete(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    //유저 찾기
    @Transactional
    public User findUserByLoginId(String loginId) {
        User user = userRepository.findByLoginId(loginId).orElseThrow(()-> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return user;
    }

    //유저가 승인된 모든 팀 목록
    public List<Team> getUserTeams(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return user.getTeams();
    }
}
