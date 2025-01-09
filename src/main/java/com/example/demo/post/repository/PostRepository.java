package com.example.demo.post.repository;

import com.example.demo.post.dto.PostType;
import com.example.demo.post.entity.Post;
import com.example.demo.team.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
        List<Post> findByTeamAndPostType(Team team, PostType postType);
}
