package com.example.demo.auth.entity;

import com.example.demo.auth.dto.UserRoleType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String loginId;
    private String password;
    private String nickname;
    private String name;
    private String Team;
    private String profileImageUrl;
    private String position;
    private String leftFoot;
    private String rightFoot;
    private String age;
    private String description;

    @Enumerated(EnumType.STRING)
    private UserRoleType userRoleType;
}

