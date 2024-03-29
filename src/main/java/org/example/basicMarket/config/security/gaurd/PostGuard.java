package org.example.basicMarket.config.security.gaurd;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.basicMarket.entity.member.RoleType;
import org.example.basicMarket.entity.post.Post;
import org.example.basicMarket.repository.post.PostRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostGuard {

    private final AuthHelper authHelper;
    private final PostRepository postRepository;

    public boolean check(Long id) {
        return authHelper.isAuthenticated() && hasAuthority(id);
    }

    private boolean hasAuthority(Long id) {
        return hasAdminRole() || isResourceOwner(id); // A || B A가 참이면 B는 실행되지도 않는다.
    }

    private boolean isResourceOwner(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> { throw new AccessDeniedException(""); });
        Long memberId = authHelper.extractMemberId();
        return post.getMember().getId().equals(memberId);
    }

    private boolean hasAdminRole() {
        return authHelper.extractMemberRoles().contains(RoleType.ROLE_ADMIN);
    }
}
