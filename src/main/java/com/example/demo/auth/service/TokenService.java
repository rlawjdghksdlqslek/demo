package com.example.demo.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final StringRedisTemplate stringRedisTemplate;
    private final RedisTemplate redisTemplate;

    public void addToBlackList(String token, long expiryMs) {
        //Redis에 토큰 저장 (만료 시간 설정)
        redisTemplate.opsForValue().set(token, "blacklisted", expiryMs, TimeUnit.MILLISECONDS);
    }

    public boolean isBlackListed(String token) {
        //Redis에서 토큰 존재 여부 확인
        return redisTemplate.hasKey(token);
    }
}
