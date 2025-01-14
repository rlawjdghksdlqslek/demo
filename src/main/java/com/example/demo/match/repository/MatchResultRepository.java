package com.example.demo.match.repository;

import com.example.demo.match.entity.MatchResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchResultRepository extends JpaRepository<MatchResult, Long> {
    MatchResult findByMatchRequestId(Long matchRequestId);
}
