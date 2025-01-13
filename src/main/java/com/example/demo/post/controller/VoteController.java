package com.example.demo.post.controller;

import com.example.demo.auth.entity.User;
import com.example.demo.auth.service.UserService;
import com.example.demo.post.dto.VoteRequest;
import com.example.demo.post.dto.VoteResponse;
import com.example.demo.post.dto.VoteStatistics;
import com.example.demo.post.service.VoteService;
import com.example.demo.utils.AuthenticatedUserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;
    private final AuthenticatedUserUtils authenticatedUserUtils;

    //투표 생성
    @PostMapping("/create")
    public ResponseEntity<String> createVote(@RequestBody VoteRequest.CreateVote createVoteRequest) {
        User author = authenticatedUserUtils.getAuthenticatedUser();
        voteService.createVote(
                createVoteRequest.getTeamId(),
                author,
                createVoteRequest.getTitle(),
                createVoteRequest.getContent(),
                createVoteRequest.getOptions(),
                createVoteRequest.getDeadline()
        );
        return ResponseEntity.ok("투표가 성공적으로 생성되었습니다.");
    }

    //투표 참여
    @PostMapping("{votePostId}/vote")
    public ResponseEntity<String> castVote(@PathVariable Long votePostId, @RequestBody VoteRequest.CastVote castVoteRequest) {
        User voter = authenticatedUserUtils.getAuthenticatedUser();
        voteService.castVote(votePostId, castVoteRequest.getOptionId(), voter);
        return ResponseEntity.ok("투표가 성공적으로 완료되었습니다.");
    }

    //투표 결과 조회
    @GetMapping("/{votePostId}/statistics")
    public ResponseEntity<VoteResponse.VoteStatisticsResponse> getVoteStatistics(@PathVariable Long votePostId) {
        VoteStatistics stats = voteService.getVoteStatistics(votePostId);
        return ResponseEntity.ok(new VoteResponse.VoteStatisticsResponse(stats));
    }

    //투표 마감
    @PostMapping("/{votePostId}/close")
    public ResponseEntity<String> closeVote(@PathVariable Long votePostId) {
        User user = authenticatedUserUtils.getAuthenticatedUser();
        voteService.closeVote(votePostId);
        return ResponseEntity.ok("투표가 마감되었습니다.");
    }
}
