package com.example.demo.post.entity;

import com.example.demo.auth.entity.User;
import com.example.demo.team.entity.Team;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VotePost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    private String title;
    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    private Date deadline;

    @OneToMany(mappedBy = "votePost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VoteOption> options;

    private boolean isClosed;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
}
