package com.example.demo.post.service;

import com.example.demo.auth.entity.User;
import com.example.demo.auth.repository.UserRepository;
import com.example.demo.post.dto.PostType;
import com.example.demo.post.dto.VoteStatistics;
import com.example.demo.post.entity.Post;
import com.example.demo.post.entity.VoteOption;
import com.example.demo.post.entity.VotePost;
import com.example.demo.post.entity.VoteRecord;
import com.example.demo.post.repository.PostRepository;
import com.example.demo.team.entity.Team;
import com.example.demo.team.repository.TeamRepository;
import com.example.demo.post.repository.VoteOptionRepository;
import com.example.demo.post.repository.VotePostRepository;
import com.example.demo.post.repository.VoteRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VotePostRepository votePostRepository;
    private final VoteOptionRepository voteOptionRepository;
    private final VoteRecordRepository voteRecordRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final PostRepository postRepository;

    //투표 생성
    @Transactional
    public void createVote(Long teamId, User author, String title, String content, List<String> options, Date deadline) {
        ZonedDateTime zonedDeadline = deadline.toInstant().atZone(ZoneId.of("Asia/Seoul"));

        if (zonedDeadline.isBefore(ZonedDateTime.now(ZoneId.of("Asia/Seoul")))) {
            throw new IllegalArgumentException("마감 시간이 현재 시간보다 이전일 수 없습니다.");
        }

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("팀을 찾을 수 없습니다."));

        VotePost votePost = VotePost.builder()
                .team(team)
                .author(author)
                .title(title)
                .content(content)
                .deadline(deadline)
                .isClosed(false)
                .createdAt(new Date())
                .updatedAt(new Date())
                .build();

        votePostRepository.save(votePost);

        //투표 옵션 저장
        options.forEach(option -> {
            VoteOption voteOption = VoteOption.builder()
                    .votePost(votePost)
                    .optionText(option)
                    .build();

            voteOptionRepository.save(voteOption);
        });
    }

    //투표 참여
    @Transactional
    public void castVote(Long votePostId, Long optionId, User user) {
        VotePost votePost = votePostRepository.findById(votePostId)
                .orElseThrow(() -> new IllegalArgumentException("투표를 찾을 수 없습니다."));

        if (votePost.isClosed()) {
            throw new IllegalArgumentException("이미 마감된 투표입니다.");
        }

        VoteOption voteOption = voteOptionRepository.findById(optionId)
                .orElseThrow(() -> new IllegalArgumentException("투표 항목을 찾을 수 없습니다."));

        //중복 투표 방지
        voteRecordRepository.deleteByVoteOptionVotePostAndUser(votePost, user);

        VoteRecord voteRecord = VoteRecord.builder()
                .voteOption(voteOption)
                .user(user)
                .votedAt(new Date())
                .build();

        voteRecordRepository.save(voteRecord);
    }

    //투표 통계
    @Transactional(readOnly = true)
    public VoteStatistics getVoteStatistics(Long votePostId) {
        VotePost votePost = votePostRepository.findById(votePostId)
                .orElseThrow(() -> new IllegalArgumentException("투표를 찾을 수 없습니다."));

        List<User> teamMembers = userRepository.findByTeam(votePost.getTeam());
        List<VoteRecord> voteRecords = voteRecordRepository.findByVoteOptionVotePost(votePost);

        Map<String, Long> voteCounts = voteRecords.stream()
                .collect(Collectors.groupingBy(vr -> vr.getVoteOption().getOptionText(), Collectors.counting()));

        List<User> nonParticipants = teamMembers.stream()
                .filter(user -> voteRecords.stream().noneMatch(vr -> vr.getUser().equals(user)))
                .toList();

        return new VoteStatistics(voteCounts, nonParticipants);
    }

    //투표 마감
    @Transactional
    public void closeVote(Long votePostId) {
        VotePost votePost = votePostRepository.findById(votePostId).orElseThrow(() -> new IllegalArgumentException("투표를 찾을 수 없습니다."));

        if (votePost.isClosed()) {
            throw new IllegalArgumentException("이미 마감된 투표입니다.");
        }

        votePost.setClosed(true);
        votePostRepository.save(votePost);

        //투표 결과 게시
        List<VoteRecord> voteRecords = voteRecordRepository.findByVoteOptionVotePost(votePost);
        Map<String,Long> voteCounts = voteRecords.stream()
                .collect(Collectors.groupingBy(vr -> vr.getVoteOption().getOptionText(), Collectors.counting()));

        String resultContent = voteCounts.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue() + "표")
                .collect(Collectors.joining("\n"));

        Team team = votePost.getTeam();
        String postTitle = "[투표 결과] " + votePost.getTitle();

        Post resultPost = Post.builder()
                .team(team)
                .author(votePost.getAuthor())
                .title(postTitle)
                .content(resultContent)
                .createdAt(new Date())
                .createdAt(new Date())
                .postType(PostType.NORMAL)
                .build();

        postRepository.save(resultPost);
    }
}
