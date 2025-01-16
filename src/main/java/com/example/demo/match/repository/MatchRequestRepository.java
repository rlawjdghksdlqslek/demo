package com.example.demo.match.repository;

import com.example.demo.match.entity.MatchRequest;
import com.example.demo.match.entity.MatchStatus;
import com.example.demo.team.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface MatchRequestRepository extends JpaRepository<MatchRequest, Long> {
    List<MatchRequest> findByTargetTeam(Team team);

    List<MatchRequest> findByTargetTeamAndMatchStatus(Team team, MatchStatus matchStatus);

    List<MatchRequest> findByRequestingTeam(Team team);

    List<MatchRequest> findByRequestingTeamAndMatchStatus(Team team, MatchStatus matchStatus);

    List<MatchRequest> findByRequestingTeamOrTargetTeam(Team requestingTeam, Team targetTeam);

    List<MatchRequest> findByMatchStatusAndLocationAndMatchFormatAndMatchDate(
            MatchStatus status, String location, String matchFormat, Date matchDate
    );

    List<MatchRequest> findByRequestingTeamId(Long teamId);
}
