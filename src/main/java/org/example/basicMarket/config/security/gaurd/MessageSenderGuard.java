package org.example.basicMarket.config.security.gaurd;

import lombok.RequiredArgsConstructor;
import org.example.basicMarket.entity.member.RoleType;
import org.example.basicMarket.entity.message.Message;
import org.example.basicMarket.exception.AccessDeniedException;
import org.example.basicMarket.repository.message.MessageRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MessageSenderGuard extends Guard {
    private final MessageRepository messageRepository;
    private List<RoleType> roleTypes = List.of(RoleType.ROLE_ADMIN);

    @Override
    protected List<RoleType> getRoleTypes() {
        return roleTypes;
    }

    @Override
    protected boolean isResourceOwner(Long id) {
        Message message = messageRepository.findById(id).orElseThrow(() -> { throw new AccessDeniedException(); });
        return message.getSender().getId().equals(AuthHelper.extractMemberId());
    }
}
