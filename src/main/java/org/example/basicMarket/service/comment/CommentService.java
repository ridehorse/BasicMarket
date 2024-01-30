package org.example.basicMarket.service.comment;

import lombok.RequiredArgsConstructor;
import org.example.basicMarket.dto.comment.CommentCreateRequest;
import org.example.basicMarket.dto.comment.CommentDto;
import org.example.basicMarket.dto.comment.CommentReadCondition;
import org.example.basicMarket.entity.comment.Comment;
import org.example.basicMarket.exception.CommentNotFoundException;
import org.example.basicMarket.repository.comment.CommentRepository;
import org.example.basicMarket.repository.member.MemberRepository;
import org.example.basicMarket.repository.post.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    public List<CommentDto> readAll(CommentReadCondition cond) { // 1
        return CommentDto.toDtoList(
                commentRepository.findAllWithMemberAndParentByPostIdOrderByParentIdAscNullsFirstCommentIdAsc(cond.getPostId())
        );
    }

    @Transactional
    public void create(CommentCreateRequest req) { // 2
        commentRepository.save(CommentCreateRequest.toEntity(req, memberRepository, postRepository, commentRepository));
    }

    @Transactional
    public void delete(Long id) { // 3
        Comment comment = commentRepository.findById(id).orElseThrow(CommentNotFoundException::new);
        comment.findDeletableComment().ifPresentOrElse(commentRepository::delete, comment::delete);
    }
}
