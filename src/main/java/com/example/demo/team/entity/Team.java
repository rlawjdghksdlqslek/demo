package com.example.demo.team.entity;

import com.example.demo.auth.entity.User;
import com.example.demo.match.entity.MatchRequest;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String teamName;
    private String description;
    private String region;
    private String logoUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "captain_id")
    private User captain; // 팀장 정보

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamMembership> memberships; // 팀 멤버십 관계

    @OneToMany(mappedBy = "requestingTeam", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MatchRequest> matchRequests; // 팀의 경기 요청

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
}
