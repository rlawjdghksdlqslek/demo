package com.example.demo.team.entity;

import com.example.demo.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamMembership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private MembershipRole role; // CAPTAIN, MANAGER, MEMBER

    @Enumerated(EnumType.STRING)
    private MembershipStatus status; // REQUESTED, INVITED, APPROVED, REJECTED

    @Temporal(TemporalType.TIMESTAMP)
    private Date joinedAt;
}




