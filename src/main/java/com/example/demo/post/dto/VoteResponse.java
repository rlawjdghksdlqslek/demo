package com.example.demo.post.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

public class VoteResponse {

    @Data
    public static class VoteStatisticsResponse {
        private Map<String, Long> voteCounts;
        private List<UserResponse> nonParticipants;

        public VoteStatisticsResponse(VoteStatistics stats) {
            this.voteCounts = stats.getVoteCounts();
            this.nonParticipants = stats.getNonParticipants().stream()
                    .map(user -> new UserResponse(user.getId(), user.getNickname()))
                    .toList();
        }
    }

    @Data
    public static class UserResponse {
        private Long id;
        private String nickname;

        public UserResponse(Long id, String nickname) {
            this.id = id;
            this.nickname = nickname;
        }
    }
}