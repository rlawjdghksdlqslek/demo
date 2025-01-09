package com.example.demo.post.service;

import com.example.demo.auth.entity.User;
import com.example.demo.auth.repository.UserRepository;
import com.example.demo.post.dto.PostResponse;
import com.example.demo.post.dto.PostType;
import com.example.demo.post.entity.Post;
import com.example.demo.post.repository.PostRepository;
import com.example.demo.team.dto.MembershipRole;
import com.example.demo.team.entity.Team;
import com.example.demo.team.entity.TeamMembership;
import com.example.demo.team.repository.TeamMembershipRepository;
import com.example.demo.team.repository.TeamRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final TeamRepository teamRepository;
    private final TeamMembershipRepository membershipRepository;

    @Transactional
    public void createPost(Long teamId, User author, String title, String content, PostType postType) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("팀을 찾을 수 없습니다."));

        // 권한 검증
        if (postType == PostType.NOTICE &&
                !team.getCaptain().equals(author) &&
                membershipRepository.findByTeamAndUser(team, author)
                        .map(TeamMembership::getRole)
                        .filter(role -> role == MembershipRole.MANAGER)
                        .isEmpty()) {
            throw new IllegalArgumentException("공지사항 작성 권한이 없습니다.");
        }

        Post post = Post.builder()
                .team(team)
                .author(author)
                .title(title)
                .content(content)
                .postType(postType)
                .createdAt(new Date())
                .updatedAt(new Date())
                .build();

        postRepository.save(post);
    }


    //게시글 타입 조회
    @Transactional(readOnly = true)
    public List<PostResponse> getPostsByType(Long teamId, PostType postType) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("팀을 찾을 수 없습니다."));

        List<Post> posts = postRepository.findByTeamAndPostType(team, postType);
        return posts.stream()
                .map(post -> new PostResponse(post.getId(), post.getTitle(), post.getContent(),
                        post.getAuthor().getNickname(), post.getCreatedAt(), post.getUpdatedAt()))
                .collect(Collectors.toList());
    }

}
