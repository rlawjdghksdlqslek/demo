package com.example.demo.team.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
public class TeamResponse {

    @Data
    public static class TeamMember {
        private Long userId;
        private String nickname;
        private String role;
        private String status;
    }

    @Data
    @AllArgsConstructor
    public static class MyTeam {
        private Long teamId;
        private String teamName;
        private String region;
    }

    @Data
    public static class TeamDetail {
        private Long teamId;
        private String teamName;
        private String description;
        private String region;
        private String captainNickname;
        private List<TeamMember> members;
    }
}
