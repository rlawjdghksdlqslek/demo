package com.example.demo.auth.service;

import com.example.demo.auth.dto.CustomUserDetails;
import com.example.demo.auth.entity.User;
import com.example.demo.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        return loadUserByLoginId(loginId);
    }

    public UserDetails loadUserByLoginId(String loginId) {
        User user = userRepository.findByLoginId(loginId).orElseThrow(() -> new UsernameNotFoundException("일치하는 유저가 없습니다." + loginId));
        return new CustomUserDetails(user,true);
    }
}
