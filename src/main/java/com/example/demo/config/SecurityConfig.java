package com.example.demo.config;

import com.example.demo.auth.jwt.JWTUtil;
import com.example.demo.auth.jwt.JWTFilter;
import com.example.demo.auth.jwt.LoginFilter;
import com.example.demo.auth.service.TokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    private final TokenService tokenService;

    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JWTUtil jwtUtil, TokenService tokenService) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtil = jwtUtil;
        this.tokenService = tokenService;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000")); //프론트 위해 3000 허용
        configuration.setAllowedMethods(Collections.singletonList("*")); // 모든 메서드 허용
        configuration.setAllowCredentials(true); //true로 설정해야 프론트엔드에서 인증 정보를 포함한 요청이 가능
        configuration.setAllowedHeaders(Collections.singletonList("*")); //클라이언트 요청에서 모든 헤더를 허용
        configuration.setExposedHeaders(Collections.singletonList("Authorization")); //클라이언트가 응답 헤더 중 "Authorization" 헤더를 읽을 수 있도록 노출
        configuration.setMaxAge(3600L); //캐시 유효 기간 3600(1시간)
        return request -> configuration;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        //csrf disable
        http
                .csrf((auth) -> auth.disable())
                //From 로그인 방식 disable
                .formLogin((auth) -> auth.disable())
                //http basic 인증 방식 disable
                .httpBasic((auth) -> auth.disable())
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/login", "/", "/join", "/api/users/register", "/api/users/login").permitAll()
                        .requestMatchers("/admin").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .addFilterBefore(new JWTFilter(jwtUtil, tokenService), LoginFilter.class)
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil), UsernamePasswordAuthenticationFilter.class)
                //세션 설정
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
