package com.example.demo.match.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MatchResponseDto {
    private Long id;
    private String teamName;
    private String matchDate;
    private String startTime;
    private String endTime;
    private String location;
    private String matchFormat;
    private String region;
}