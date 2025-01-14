package com.example.demo.match.dto;

import lombok.Data;

import java.util.Date;

@Data
public class MatchRequestDto {
    private Long requestingTeamId;
    private Long targetTeamId;
    private Date matchDate;
    private Date startTime;
    private Date endTime;
    private String location;
    private String matchFormat;
}