package com.example.demo.match.repository;

import com.example.demo.match.entity.MatchApplication;
import com.example.demo.match.entity.MatchRequest;
import com.example.demo.team.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MatchApplicationRepository extends JpaRepository<MatchApplication, Long> {
    List<MatchApplication> findByMatchRequest(MatchRequest matchRequest);
    Optional<MatchApplication> findByMatchRequestAndApplicantTeam(MatchRequest matchRequest, Team applicantTeam);
}
