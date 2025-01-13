package com.example.demo.post.repository;

import com.example.demo.auth.entity.User;
import com.example.demo.post.entity.VotePost;
import com.example.demo.post.entity.VoteRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VoteRecordRepository extends JpaRepository<VoteRecord, Long> {
    // 특정 투표 게시글에 대한 모든 투표 기록 검색
    List<VoteRecord> findByVoteOptionVotePost(VotePost votePost);

    // 특정 사용자와 투표 게시글에 대한 투표 기록 삭제 (중복 방지용)
    void deleteByVoteOptionVotePostAndUser(VotePost votePost, User user);
}
