package com.example.demo.post.scheduler;

import com.example.demo.post.entity.VotePost;
import com.example.demo.post.repository.VotePostRepository;
import com.example.demo.post.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class VoteScheduler {

    private final VotePostRepository votePostRepository;
    private final VoteService voteService;

    @Scheduled(fixedRate = 60000) // 1분 간격 실행
    public void checkAndCloseExpiredVotes() {
        Date now = new Date();

        // 마감 시간이 지나고 아직 마감되지 않은 투표 조회
        List<VotePost> expiredVotes = votePostRepository.findByDeadlineBeforeAndIsClosedFalse(now);

        for (VotePost vote : expiredVotes) {
            try {
                voteService.closeVote(vote.getId());
                System.out.println("투표 마감: " + vote.getId());
            } catch (Exception e) {
                System.err.println("투표 마감 처리 실패: " + vote.getId());
                e.printStackTrace();
            }
        }
    }
}
