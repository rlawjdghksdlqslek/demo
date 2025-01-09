package com.example.demo.post.controller;

import com.example.demo.auth.entity.User;
import com.example.demo.auth.service.UserService;
import com.example.demo.post.dto.PostRequest;
import com.example.demo.post.dto.PostResponse;
import com.example.demo.post.dto.PostType;
import com.example.demo.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final UserService userService;


    //게시글 작성
    @PostMapping("/{teamId}/posts")
    public ResponseEntity<String> createPost(@PathVariable Long teamId,
                                             @RequestBody PostRequest postRequest) {
        User author = getAuthenticatedUser();
        postService.createPost(teamId, author, postRequest.getTitle(), postRequest.getContent(), postRequest.getPostType());
        return ResponseEntity.ok("게시글이 작성되었습니다.");
    }

    //공지사항 조회
    @GetMapping("/{teamId}/posts/notice")
    public ResponseEntity<List<PostResponse>> getNotices(@PathVariable Long teamId) {
        List<PostResponse> notices = postService.getPostsByType(teamId, PostType.NOTICE);
        return ResponseEntity.ok(notices);
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginId = authentication.getName();
        return userService.findUserByLoginId(loginId);
    }
}
