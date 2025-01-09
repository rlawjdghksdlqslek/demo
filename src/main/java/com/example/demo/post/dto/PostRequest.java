package com.example.demo.post.dto;

import lombok.Data;

@Data
public class PostRequest {
        private String title;
        private String content;
        private PostType postType; // NOTICE or NORMAL
}
