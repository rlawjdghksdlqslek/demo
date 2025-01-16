package com.example.demo.match.entity;

import com.example.demo.team.entity.Team;
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
public class MatchRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requesting_team_id")
    private Team requestingTeam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_team_id")
    private Team targetTeam;

    @Temporal(TemporalType.DATE)
    private Date matchDate;

    @Temporal(TemporalType.TIME)
    private Date startTime;

    @Temporal(TemporalType.TIME)
    private Date endTime;

    private String location;

    private String matchFormat; // 경기 형식 (예: 5vs5, 11vs11)

    @Enumerated(EnumType.STRING)
    private MatchStatus matchStatus; // (OPEN, CLOSED, CANCELED)

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @OneToMany(mappedBy = "matchRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MatchApplication> applications; // 신청 리스트

    @OneToOne
    @JoinColumn(name = "confirmed_application_id", referencedColumnName = "id")
    private MatchApplication confirmedApplication; // 확정된 매칭 신청
}
