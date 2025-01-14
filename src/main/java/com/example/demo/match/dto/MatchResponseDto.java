package com.example.demo.match.dto;

import com.example.demo.match.entity.MatchStatus;
import lombok.Data;

@Data
public class MatchResponseDto {
    private MatchStatus status;
    private String rejectReason;
}