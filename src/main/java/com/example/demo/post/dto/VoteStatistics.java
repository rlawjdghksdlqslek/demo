package com.example.demo.post.dto;

import com.example.demo.auth.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class VoteStatistics {
    private Map<String, Long> voteCounts;
    private List<User> nonParticipants;
}
