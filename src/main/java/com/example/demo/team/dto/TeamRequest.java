package com.example.demo.team.dto;

import com.example.demo.team.entity.MembershipRole;
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

    @Data
    public static class AssignRole {
        private Long userId;
        private MembershipRole role;
    }
}
