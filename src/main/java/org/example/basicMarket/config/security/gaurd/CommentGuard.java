package org.example.basicMarket.config.security.gaurd;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.basicMarket.entity.comment.Comment;
import org.example.basicMarket.entity.member.RoleType;
import org.example.basicMarket.repository.comment.CommentRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommentGuard {
    private final AuthHelper authHelper;
    private final CommentRepository commentRepository;

    public boolean check(Long id) {
        return authHelper.isAuthenticated() && hasAuthority(id);
    }

    private boolean hasAuthority(Long id) {
        return hasAdminRole() || isResourceOwner(id);
    }

    private boolean isResourceOwner(Long id) {
        Comment comment = commentRepository.findById(id).orElseThrow(() -> { throw new AccessDeniedException(""); });
        Long memberId = authHelper.extractMemberId();
        return comment.getMember().getId().equals(memberId);
    }

    private boolean hasAdminRole() {
        return authHelper.extractMemberRoles().contains(RoleType.ROLE_ADMIN);
    }
}
