package com.example.demo.match.service;

import com.example.demo.auth.entity.User;
import com.example.demo.match.dto.MatchResponseDto;
import com.example.demo.match.entity.MatchApplication;
import com.example.demo.match.entity.MatchRequest;
import com.example.demo.match.entity.MatchResult;
import com.example.demo.match.entity.MatchStatus;
import com.example.demo.match.repository.MatchApplicationRepository;
import com.example.demo.match.repository.MatchRequestRepository;
import com.example.demo.match.repository.MatchResultRepository;
import com.example.demo.team.entity.Team;
import com.example.demo.team.repository.TeamMembershipRepository;
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
    private final MatchApplicationRepository matchApplicationRepository;
    private final TeamRepository teamRepository;
    private final TeamMembershipRepository teamMembershipRepository;

    // 경기 요청 생성
    @Transactional
    public MatchResponseDto createMatchRequest(User user, Date matchDate, Date startTime, Date endTime, String location, String matchFormat) {
        Team requestingTeam = teamMembershipRepository.findApprovedTeamByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 속한 팀이 없습니다."));

        MatchRequest matchRequest = MatchRequest.builder()
                .requestingTeam(requestingTeam)
                .matchDate(matchDate)
                .startTime(startTime)
                .endTime(endTime)
                .location(location)
                .matchFormat(matchFormat)
                .matchStatus(MatchStatus.OPEN)
                .createdAt(new Date())
                .updatedAt(new Date())
                .build();

        MatchRequest savedMatchRequest = matchRequestRepository.save(matchRequest);

        return MatchResponseDto.builder()
                .id(savedMatchRequest.getId())
                .matchDate(matchRequest.getMatchDate().toString())
                .startTime(matchRequest.getStartTime().toString())
                .endTime(matchRequest.getEndTime().toString())
                .location(matchRequest.getLocation())
                .matchFormat(matchRequest.getMatchFormat())
                .teamName(matchRequest.getRequestingTeam().getTeamName())
                .region(matchRequest.getRequestingTeam().getRegion())
                .build();
    }

    // 생성된 경기 리스트 조회
    @Transactional(readOnly = true)
    public List<MatchRequest> getMatchRequests(String location, String matchFormat, Date matchDate) {
        return matchRequestRepository.findByMatchStatusAndLocationAndMatchFormatAndMatchDate(
                MatchStatus.OPEN, location, matchFormat, matchDate
        );
    }

    // 경기 요청에 신청
    @Transactional
    public MatchApplication applyToMatchRequest(Long matchRequestId, Long applicantTeamId, String comment) {
        MatchRequest matchRequest = matchRequestRepository.findById(matchRequestId)
                .orElseThrow(() -> new IllegalArgumentException("경기 요청을 찾을 수 없습니다."));

        Team applicantTeam = teamRepository.findById(applicantTeamId)
                .orElseThrow(() -> new IllegalArgumentException("신청 팀을 찾을 수 없습니다."));

        if (matchApplicationRepository.findByMatchRequestAndApplicantTeam(matchRequest, applicantTeam).isPresent()) {
            throw new IllegalArgumentException("이미 신청한 팀입니다.");
        }

        MatchApplication application = MatchApplication.builder()
                .matchRequest(matchRequest)
                .applicantTeam(applicantTeam)
                .comment(comment)
                .build();

        return matchApplicationRepository.save(application);
    }

    // 경기 신청 리스트 조회
    @Transactional(readOnly = true)
    public List<MatchApplication> getApplicationsForMatchRequest(Long matchRequestId) {
        MatchRequest matchRequest = matchRequestRepository.findById(matchRequestId)
                .orElseThrow(() -> new IllegalArgumentException("경기 요청을 찾을 수 없습니다."));

        return matchApplicationRepository.findByMatchRequest(matchRequest);
    }

    // 매칭 확정
    @Transactional
    public MatchRequest confirmMatch(Long matchRequestId, Long applicationId) { // applicationId는 특정 경기 요청에 대해 신청한 팀
        MatchRequest matchRequest = matchRequestRepository.findById(matchRequestId)
                .orElseThrow(() -> new IllegalArgumentException("경기 요청을 찾을 수 없습니다."));

        if (!matchRequest.getMatchStatus().equals(MatchStatus.OPEN)) {
            throw new IllegalArgumentException("이미 마감된 경기 요청입니다.");
        }

        MatchApplication application = matchApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("매칭 신청을 찾을 수 없습니다."));

        if (!application.getMatchRequest().equals(matchRequest)) {
            throw new IllegalArgumentException("신청이 경기 요청과 일치하지 않습니다.");
        }

        application.setIsConfirmed(true);
        matchRequest.setConfirmedApplication(application);
        matchRequest.setMatchStatus(MatchStatus.CLOSED);
        matchRequest.setUpdatedAt(new Date());

        matchApplicationRepository.save(application);
        return matchRequestRepository.save(matchRequest);
    }

    //경기 결과 저장
    @Transactional
    public MatchResult saveMatchResult(Long matchRequestId, Integer requestingTeamScore, Integer targetTeamScore, String goalScorers) {
        MatchRequest matchRequest = matchRequestRepository.findById(matchRequestId)
                .orElseThrow(() -> new IllegalArgumentException("매칭 요청을 찾을 수 없습니다."));

        if (matchRequest.getMatchStatus() != MatchStatus.CLOSED) {
            throw new IllegalArgumentException("마감된 매칭만 결과를 저장할 수 있습니다.");
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

    // 전체 경기 조회
    @Transactional(readOnly = true)
    public List<MatchRequest> getAllMatchRequests() {
        return matchRequestRepository.findAll();
    }
}
