package com.example.demo.auth.entity;

import com.example.demo.auth.dto.UserRoleType;
import com.example.demo.team.dto.MembershipStatus;
import com.example.demo.team.entity.Team;
import com.example.demo.team.entity.TeamMembership;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

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
    private String profileImageUrl;
    private String position;
    private String leftFoot;
    private String rightFoot;
    private String age;
    private String description;

    @Enumerated(EnumType.STRING)
    private UserRoleType userRoleType;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamMembership> memberships; // 유저가 가입한 팀의 정보
}

