package com.example.demo.auth.dto;

import com.example.demo.auth.entity.UserRoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserRequest {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class register{
        private String loginId;
        private String password;
        private String nickname;
        private String name;
        private String profileImageUrl;
        private String position;
        private String leftFoot;
        private String rightFoot;
        private String age;
        private String description;
        private UserRoleType userRoleType;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class login{
        private String loginId;
        private String password;
    }
}
