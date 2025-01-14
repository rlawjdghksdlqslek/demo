package com.example.demo.match.dto;

import lombok.Data;

@Data
public class MatchResultDto {

    private Integer requestingTeamScore;
    private Integer targetTeamScore;
    private String goalScorers;
}