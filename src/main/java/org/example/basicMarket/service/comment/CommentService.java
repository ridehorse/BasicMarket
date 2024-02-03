package org.example.basicMarket.service.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.basicMarket.dto.comment.CommentCreateRequest;
import org.example.basicMarket.dto.comment.CommentDto;
import org.example.basicMarket.dto.comment.CommentReadCondition;
import org.example.basicMarket.entity.comment.Comment;
import org.example.basicMarket.exception.CommentNotFoundException;
import org.example.basicMarket.repository.comment.CommentRepository;
import org.example.basicMarket.repository.member.MemberRepository;
import org.example.basicMarket.repository.post.PostRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final ApplicationEventPublisher publisher;

    public List<CommentDto> readAll(CommentReadCondition cond) { // 1
        return CommentDto.toDtoList(
                commentRepository.findAllWithMemberAndParentByPostIdOrderByParentIdAscNullsFirstCommentIdAsc(cond.getPostId())
        );
    }

    @Transactional
    public void create(CommentCreateRequest req) { // 2
        log.info("CommentService.create 진입");
        Comment comment = commentRepository.save(CommentCreateRequest.toEntity(req, memberRepository, postRepository, commentRepository));
        comment.publishCreatedEvent(publisher);
        log.info("CommentService.create");
    }

    @Transactional
    public void delete(Long id) { // 3
        Comment comment = commentRepository.findById(id).orElseThrow(CommentNotFoundException::new);
        comment.findDeletableComment().ifPresentOrElse(commentRepository::delete, comment::delete);
    }
}
