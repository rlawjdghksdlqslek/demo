package com.example.demo.match.service;

import com.example.demo.auth.entity.User;
import com.example.demo.match.entity.MatchRequest;
import com.example.demo.match.entity.MatchResult;
import com.example.demo.match.entity.MatchStatus;
import com.example.demo.match.repository.MatchRequestRepository;
import com.example.demo.match.repository.MatchResultRepository;
import com.example.demo.team.entity.Team;
import com.example.demo.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRequestRepository matchRequestRepository;
    private final MatchResultRepository matchResultRepository;
    private final TeamRepository teamRepository;

    //경기 요청
    @Transactional
    public MatchRequest matchRequest(Long requestingTeamId, Long targetTeamId, Date matchDate, Date startTime, Date endTime, String Location, String matchFormat) {
        Team requestingTeam = teamRepository.findById(requestingTeamId).orElseThrow(() -> new IllegalArgumentException("요청 팀을 찾을 수 없습니다."));
        Team targetTeam = teamRepository.findById(targetTeamId).orElseThrow(() -> new IllegalArgumentException("상대 팀을 찾을 수 없습니다."));

        //권한 확인 추가

        MatchRequest matchRequest = MatchRequest.builder()
                .requestingTeam(requestingTeam)
                .targetTeam(targetTeam)
                .matchDate(matchDate)
                .startTime(startTime)
                .endTime(endTime)
                .location(Location)
                .matchFormat(matchFormat)
                .matchStatus(MatchStatus.PENDING)
                .createdAt(new Date())
                .updatedAt(new Date())
                .build();

        return matchRequestRepository.save(matchRequest);
    }

    //경기 요청 상태 업데이트 (수락/거절)
    @Transactional
    public void respondToMatchRequest(Long matchRequestId, MatchStatus matchStatus, String rejectReason) {
        MatchRequest matchRequest = matchRequestRepository.findById(matchRequestId)
                .orElseThrow(() -> new IllegalArgumentException("매칭 요청을 찾을 수 없습니다."));

        if (matchStatus == MatchStatus.REJECTED && (rejectReason == null || rejectReason.isEmpty())) {
            throw new IllegalArgumentException("거절 사유를 입력해야 합니다.");
        }

        matchRequest.setMatchStatus(matchStatus);
        if (matchStatus == MatchStatus.REJECTED) {
            matchRequest.setRejectReason(rejectReason);
        }
        matchRequest.setUpdatedAt(new Date());
        matchRequestRepository.save(matchRequest);
    }

    //경기 결과 저장
    @Transactional
    public MatchResult saveMatchResult(Long matchRequestId, Integer requestingTeamScore, Integer targetTeamScore, String goalScorers) {
        MatchRequest matchRequest = matchRequestRepository.findById(matchRequestId)
                .orElseThrow(() -> new IllegalArgumentException("매칭 요청을 찾을 수 없습니다."));

        if (matchRequest.getMatchStatus() != MatchStatus.ACCEPTED) {
            throw new IllegalArgumentException("수락된 매칭만 결과를 저장할 수 있습니다.");
        }

        MatchResult matchResult = MatchResult.builder()
                .matchRequest(matchRequest)
                .requestingTeamScore(requestingTeamScore)
                .targetTeamScore(targetTeamScore)
                .goalScorers(goalScorers)
                .createdAt(new Date())
                .build();

        return matchResultRepository.save(matchResult);
    }

    // 팀별 경기 기록 조회
    @Transactional(readOnly = true)
    public List<MatchRequest> getMatchHistory(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("팀을 찾을 수 없습니다."));
        return matchRequestRepository.findByRequestingTeamOrTargetTeam(team, team);
    }

    // 받은 경기 요청 조회
    @Transactional(readOnly = true)
    public List<MatchRequest> getReceivedMatchRequests(Long teamId, String status) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("팀을 찾을 수 없습니다."));

        if (status != null) {
            MatchStatus matchStatus = MatchStatus.valueOf(status.toUpperCase());
            return matchRequestRepository.findByTargetTeamAndMatchStatus(team, matchStatus);
        } else {
            return matchRequestRepository.findByTargetTeam(team);
        }
    }

    // 보낸 경기 요청 조회
    @Transactional(readOnly = true)
    public List<MatchRequest> getSentMatchRequests(Long teamId, String status) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("팀을 찾을 수 없습니다."));

        if (status != null) {
            MatchStatus matchStatus = MatchStatus.valueOf(status.toUpperCase());
            return matchRequestRepository.findByRequestingTeamAndMatchStatus(team, matchStatus);
        } else {
            return matchRequestRepository.findByRequestingTeam(team);
        }
    }
}
