package com.example.demo.team.dto;

import lombok.*;

public class TeamRequest {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateTeam {
        private String teamName;
        private String description;
        private String region;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApproveJoin {
        private Long userId;
    }
}
