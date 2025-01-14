package com.example.demo.match.entity;
import com.example.demo.team.entity.Team;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_request_id")
    private MatchRequest matchRequest;

    private Integer requestingTeamScore; // 요청 팀 점수
    private Integer targetTeamScore; // 상대 팀 점수

    private String goalScorers; // 득점자 정보

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
}
