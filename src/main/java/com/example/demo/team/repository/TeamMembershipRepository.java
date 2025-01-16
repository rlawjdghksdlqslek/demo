package com.example.demo.team.repository;

import com.example.demo.auth.entity.User;
import com.example.demo.team.entity.MembershipStatus;
import com.example.demo.team.entity.Team;
import com.example.demo.team.entity.TeamMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TeamMembershipRepository extends JpaRepository<TeamMembership, Long> {
    // 특정 유저가 가입한 모든 팀 멤버십 조회
    List<TeamMembership> findByUser(User user);

    // 특정 팀의 모든 멤버십 조회
    List<TeamMembership> findByTeam(Team team);

    // 특정 팀과 유저의 멤버십 상태 조회
    Optional<TeamMembership> findByTeamAndUser(Team team, User user);

    // 특정 팀 ID와 유저 ID로 멤버십 조회
    Optional<TeamMembership> findByTeamIdAndUserId(Long teamId, Long userId);

    // 특정 팀의 승인된 멤버 목록 조회
    List<TeamMembership> findByTeamAndStatus(Team team, MembershipStatus status);

    // 유저가 속한 팀을 조회.
    @Query("SELECT m.team FROM TeamMembership m WHERE m.user = :user AND m.status = 'APPROVED'")
    Optional<Team> findApprovedTeamByUser(User user);
}
