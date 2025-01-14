package com.example.demo.match.controller;

import com.example.demo.match.dto.MatchRequestDto;
import com.example.demo.match.dto.MatchResponseDto;
import com.example.demo.match.dto.MatchResultDto;
import com.example.demo.match.entity.MatchRequest;
import com.example.demo.match.entity.MatchResult;
import com.example.demo.match.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    // 경기 요청 생성
    @PostMapping("/request")
    public ResponseEntity<MatchRequest> createMatchRequest(@RequestBody MatchRequestDto request) {
        MatchRequest matchRequest = matchService.matchRequest(
                request.getRequestingTeamId(),
                request.getTargetTeamId(),
                request.getMatchDate(),
                request.getStartTime(),
                request.getEndTime(),
                request.getLocation(),
                request.getMatchFormat()
        );
        return ResponseEntity.ok(matchRequest);
    }

    // 경기 요청 응답
    @PostMapping("/{matchRequestId}/respond")
    public ResponseEntity<String> respondToMatchRequest(@PathVariable Long matchRequestId,
                                                        @RequestBody MatchResponseDto response) {
        matchService.respondToMatchRequest(matchRequestId, response.getStatus(), response.getRejectReason());
        return ResponseEntity.ok("매칭 요청 상태가 업데이트되었습니다.");
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
