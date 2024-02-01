package org.example.basicMarket.config.security.gaurd;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.basicMarket.entity.comment.Comment;
import org.example.basicMarket.entity.member.RoleType;
import org.example.basicMarket.repository.comment.CommentRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CommentGuard extends Guard {
    private final CommentRepository commentRepository;
    private List<RoleType> roleTypes = List.of(RoleType.ROLE_ADMIN);

    @Override
    protected List<RoleType> getRoleTypes() {
        return roleTypes;
    }

    @Override
    protected boolean isResourceOwner(Long id) {
        Comment comment = commentRepository.findById(id).orElseThrow(() -> { throw new AccessDeniedException(""); });
        Long memberId = AuthHelper.extractMemberId();
        return comment.getMember().getId().equals(memberId);
    }
}
