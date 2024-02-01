package org.example.basicMarket.config.security.gaurd;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.basicMarket.entity.member.RoleType;
import org.example.basicMarket.entity.post.Post;
import org.example.basicMarket.repository.post.PostRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PostGuard extends Guard {
    private final PostRepository postRepository;
    private List<RoleType> roleTypes = List.of(RoleType.ROLE_ADMIN);

    @Override
    protected List<RoleType> getRoleTypes() {
        return roleTypes;
    }

    @Override
    protected boolean isResourceOwner(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> { throw new AccessDeniedException(""); });
        Long memberId = AuthHelper.extractMemberId();
        return post.getMember().getId().equals(memberId);
    }
}
