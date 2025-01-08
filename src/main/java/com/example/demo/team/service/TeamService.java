package com.example.demo.team.service;

import com.example.demo.auth.entity.User;
import com.example.demo.auth.repository.UserRepository;
import com.example.demo.team.dto.MembershipRole;
import com.example.demo.team.dto.MembershipStatus;
import com.example.demo.team.entity.Team;
import com.example.demo.team.entity.TeamMembership;
import com.example.demo.team.repository.TeamMembershipRepository;
import com.example.demo.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMembershipRepository membershipRepository;

    @Transactional
    public Team createTeam(String teamName, String description, String region, User captain) {
        if (teamRepository.findByTeamName(teamName).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 팀 이름입니다.");
        }

        Team team = Team.builder()
                .teamName(teamName)
                .description(description)
                .region(region)
                .captain(captain)
                .createdAt(new Date())
                .updatedAt(new Date())
                .build();

        teamRepository.save(team);

        TeamMembership captainMembership = TeamMembership.builder()
                .team(team)
                .user(captain)
                .role(MembershipRole.CAPTAIN)
                .status(MembershipStatus.APPROVED)
                .joinedAt(new Date())
                .build();

        membershipRepository.save(captainMembership);

        return team;
    }

    //팀 가입 신청
    @Transactional
    public void requestJoinTeam(Long teamId, User user) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("팀을 찾을 수 없습니다."));

        if (membershipRepository.findByTeamAndUser(team, user).isPresent()) {
            throw new IllegalArgumentException("이미 가입 요청이 존재합니다.");
        }

        TeamMembership membership = TeamMembership.builder()
                .team(team)
                .user(user)
                .role(MembershipRole.MEMBER)
                .status(MembershipStatus.REQUESTED) // 요청 상태로 설정
                .joinedAt(null) // 가입 확정되지 않았으므로 null
                .build();

        membershipRepository.save(membership);
    }

    // 가입 신청 승인
    @Transactional
    public void approveJoinRequest(Long teamId, Long userId, User captain) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("팀을 찾을 수 없습니다."));

        if (!team.getCaptain().equals(captain)) {
            throw new IllegalArgumentException("가입 요청을 승인할 권한이 없습니다.");
        }

        TeamMembership membership = membershipRepository.findByTeamIdAndUserId(teamId, userId)
                .orElseThrow(() -> new IllegalArgumentException("가입 요청을 찾을 수 없습니다."));

        if (!membership.getStatus().equals(MembershipStatus.REQUESTED)) {
            throw new IllegalArgumentException("가입 요청 상태가 아닙니다.");
        }

        membership.setStatus(MembershipStatus.APPROVED);
        membership.setJoinedAt(new Date());

        membershipRepository.save(membership);
    }

    //팀 유저 조회
    @Transactional
    public List<Team> getUserTeams(User user) {
        return membershipRepository.findByUser(user).stream()
                .filter(membership -> membership.getStatus() == MembershipStatus.APPROVED)
                .map(TeamMembership::getTeam)
                .collect(Collectors.toList());
    }

    //팀 삭제
    @Transactional
    public void deleteTeam(Long teamId, User captain) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("팀을 찾을 수 없습니다."));

        if (!team.getCaptain().equals(captain)) {
            throw new IllegalArgumentException("팀 삭제 권한이 없습니다.");
        }

        teamRepository.delete(team);
    }

}
