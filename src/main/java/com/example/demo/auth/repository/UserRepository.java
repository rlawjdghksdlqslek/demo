package com.example.demo.auth.repository;

import com.example.demo.auth.entity.User;
import com.example.demo.team.entity.Team;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLoginId(String loginId);

    // 특정 팀에 속한 모든 사용자 검색
    @Query("SELECT u FROM User u JOIN u.memberships m WHERE m.team = :team AND m.status = 'APPROVED'")
    List<User> findByTeam(@Param("team") Team team);
}
