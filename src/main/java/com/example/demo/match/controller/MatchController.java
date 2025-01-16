package com.example.demo.match.controller;

import com.example.demo.auth.entity.User;
import com.example.demo.auth.service.UserService;
import com.example.demo.match.dto.MatchRequestDto;
import com.example.demo.match.dto.MatchResponseDto;
import com.example.demo.match.dto.MatchResultDto;
import com.example.demo.match.entity.MatchApplication;
import com.example.demo.match.entity.MatchRequest;
import com.example.demo.match.entity.MatchResult;
import com.example.demo.match.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;
    private final UserService userService;

    // 경기 생성
    @PostMapping("/request")
    public ResponseEntity<MatchResponseDto> createMatchRequest(@RequestBody MatchRequestDto request, Authentication authentication) {
        User currentUser = userService.findUserByLoginId(authentication.getName());

        MatchResponseDto responseDto = matchService.createMatchRequest(
                currentUser,
                request.getMatchDate(),
                request.getStartTime(),
                request.getEndTime(),
                request.getLocation(),
                request.getMatchFormat()
        );
        return ResponseEntity.ok(responseDto);
    }

    // 경기 요청 리스트 조회
    @GetMapping
    public ResponseEntity<List<MatchRequest>> getMatchRequests(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String matchFormat,
            @RequestParam(required = false) Date matchDate
    ) {
        List<MatchRequest> matchRequests;

        if (location == null && matchFormat == null && matchDate == null) {
            matchRequests = matchService.getAllMatchRequests();
        } else {
            matchRequests = matchService.getMatchRequests(location, matchFormat, matchDate);
        }

        return ResponseEntity.ok(matchRequests);
    }

    // 매칭 확정
    @PostMapping("/{matchRequestId}/confirm")
    public ResponseEntity<MatchRequest> confirmMatch(
            @PathVariable Long matchRequestId,
            @RequestParam Long applicationId) {
        MatchRequest matchRequest = matchService.confirmMatch(matchRequestId, applicationId);
        return ResponseEntity.ok(matchRequest);
    }

    // 경기 신청
    @PostMapping("/{matchRequestId}/apply")
    public ResponseEntity<MatchApplication> applyToMatchRequest(
            @PathVariable Long matchRequestId,
            @RequestParam Long applicantTeamId,
            @RequestParam(required = false) String comment) {
        MatchApplication application = matchService.applyToMatchRequest(matchRequestId, applicantTeamId, comment);
        return ResponseEntity.ok(application);
    }

    // 경기 신청 리스트 조회
    @GetMapping("/{matchRequestId}/applications")
    public ResponseEntity<List<MatchApplication>> getApplicationsForMatchRequest(
            @PathVariable Long matchRequestId) {
        List<MatchApplication> applications = matchService.getApplicationsForMatchRequest(matchRequestId);
        return ResponseEntity.ok(applications);
    }

    // 경기 결과 저장
    @PostMapping("/{matchRequestId}/result")
    public ResponseEntity<MatchResult> saveMatchResult(@PathVariable Long matchRequestId,
                                                       @RequestBody MatchResultDto result) {
        MatchResult matchResult = matchService.saveMatchResult(
                matchRequestId,
                result.getRequestingTeamScore(),
                result.getTargetTeamScore(),
                result.getGoalScorers()
        );
        return ResponseEntity.ok(matchResult);
    }

    // 팀별 경기 기록 조회
    @GetMapping("/{teamId}/history")
    public ResponseEntity<List<MatchRequest>> getMatchHistory(@PathVariable Long teamId) {
        List<MatchRequest> history = matchService.getMatchHistory(teamId);
        return ResponseEntity.ok(history);
    }

    // 받은 경기 요청 조회
    @GetMapping("/{teamId}/received-requests")
    public ResponseEntity<List<MatchRequest>> getReceivedMatchRequests(
            @PathVariable Long teamId,
            @RequestParam(required = false) String status) {
        List<MatchRequest> matchRequests = matchService.getReceivedMatchRequests(teamId, status);
        return ResponseEntity.ok(matchRequests);
    }

    // 보낸 경기 요청 조회
    @GetMapping("/{teamId}/sent-requests")
    public ResponseEntity<List<MatchRequest>> getSentMatchRequests(
            @PathVariable Long teamId,
            @RequestParam(required = false) String status) {
        List<MatchRequest> matchRequests = matchService.getSentMatchRequests(teamId, status);
        return ResponseEntity.ok(matchRequests);
    }
}
