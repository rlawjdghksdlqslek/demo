package com.example.demo.match.entity;

import com.example.demo.team.entity.Team;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_request_id")
    private MatchRequest matchRequest; // 경기 요청

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_team_id")
    private Team applicantTeam; // 신청 팀

    private String comment; // 신청 코멘트

    private Boolean isConfirmed; // 확정 여부
}
