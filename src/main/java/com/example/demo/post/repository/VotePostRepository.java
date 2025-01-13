package com.example.demo.post.repository;

import com.example.demo.post.entity.VotePost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface VotePostRepository extends JpaRepository<VotePost, Long> {

    // 마감 시간이 지나고 아직 마감되지 않은 투표 조회
    List<VotePost> findByDeadlineBeforeAndIsClosedFalse(Date now);
}
