package com.example.demo.team.controller;

import com.example.demo.auth.dto.CustomUserDetails;
import com.example.demo.auth.entity.User;
import com.example.demo.auth.service.UserService;
import com.example.demo.team.dto.TeamRequest;
import com.example.demo.team.dto.TeamResponse;
import com.example.demo.team.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;
    private final UserService userService;

    // 팀 생성
    @PostMapping("/create")
    public ResponseEntity<String> createTeam(@RequestBody TeamRequest.CreateTeam createTeamRequest) {
        User captain = getAuthenticatedUser();
        teamService.createTeam(createTeamRequest.getTeamName(), createTeamRequest.getDescription(), createTeamRequest.getRegion(), captain);
        return ResponseEntity.ok("팀이 성공적으로 생성되었습니다.");
    }

    // 팀 가입 요청
    @PostMapping("/{teamId}/join")
    public ResponseEntity<String> requestJoinTeam(@PathVariable Long teamId) {
        User user = getAuthenticatedUser();
        teamService.requestJoinTeam(teamId, user);
        return ResponseEntity.ok("가입 요청이 성공적으로 처리되었습니다.");
    }

    // 가입 요청 승인
    @PostMapping("/{teamId}/approve")
    public ResponseEntity<String> approveJoinRequest(@PathVariable Long teamId, @RequestBody TeamRequest.ApproveJoin approveJoinRequest) {
        User captain = getAuthenticatedUser();
        teamService.approveJoinRequest(teamId, approveJoinRequest.getUserId(), captain);
        return ResponseEntity.ok("가입 요청이 승인되었습니다.");
    }

    // 특정 팀의 멤버 조회
    @GetMapping("/my-teams")
    public ResponseEntity<List<TeamResponse.MyTeam>> getUserTeams() {
        User user = getAuthenticatedUser();
        List<TeamResponse.MyTeam> teams = teamService.getUserTeams(user).stream()
                .map(team -> new TeamResponse.MyTeam(team.getId(), team.getTeamName(), team.getRegion()))
                .toList();
        return ResponseEntity.ok(teams);
    }

    // 팀 삭제
    @DeleteMapping("/{teamId}")
    public ResponseEntity<String> deleteTeam(@PathVariable Long teamId) {
        User captain = getAuthenticatedUser();
        teamService.deleteTeam(teamId, captain);
        return ResponseEntity.ok("팀이 성공적으로 삭제되었습니다.");
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginId = authentication.getName();
        return userService.findUserByLoginId(loginId);
    }
}
