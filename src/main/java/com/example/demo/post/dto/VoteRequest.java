package com.example.demo.post.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

public class VoteRequest {

    @Data
    public static class CreateVote {
        private Long teamId;
        private String title;
        private String content;
        private List<String> options;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
        private Date deadline;
    }

    @Data
    public static class CastVote {
        private Long optionId;
    }
}