package org.example.basicMarket.service.message;

import lombok.RequiredArgsConstructor;
import org.example.basicMarket.dto.message.MessageCreateRequest;
import org.example.basicMarket.dto.message.MessageDto;
import org.example.basicMarket.dto.message.MessageListDto;
import org.example.basicMarket.dto.message.MessageReadCondition;
import org.example.basicMarket.entity.message.Message;
import org.example.basicMarket.exception.MessageNotFoundException;
import org.example.basicMarket.repository.member.MemberRepository;
import org.example.basicMarket.repository.message.MessageRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final MemberRepository memberRepository;

    public MessageListDto readAllBySender(MessageReadCondition cond) { // 1
        return MessageListDto.toDto(
                messageRepository.findAllBySenderIdOrderByMessageIdDesc(cond.getMemberId(), cond.getLastMessageId(), Pageable.ofSize(cond.getSize()))
        );
    }

    public MessageListDto readAllByReceiver(MessageReadCondition cond) { // 1
        return MessageListDto.toDto(
                messageRepository.findAllByReceiverIdOrderByMessageIdDesc(cond.getMemberId(), cond.getLastMessageId(), Pageable.ofSize(cond.getSize()))
        );
    }

    public MessageDto read(Long id) { // 2
        return MessageDto.toDto(
                messageRepository.findWithSenderAndReceiverById(id).orElseThrow(MessageNotFoundException::new)
        );
    }

    @Transactional
    public void create(MessageCreateRequest req) { // 3
        messageRepository.save(MessageCreateRequest.toEntity(req, memberRepository));
    }

    @Transactional
    public void deleteBySender(Long id) { // 4
        delete(id, Message::deleteBySender);
    }

    @Transactional
    public void deleteByReceiver(Long id) { // 4
        delete(id, Message::deleteByReceiver);
    }

    // delete 필드에는 Message::deleteByReceiver 수식이 들어간다.이 수식은 Message 객체의 deleteBYReceiver() 매서드를 말한다.
    // 그런데, Message는 어떤 인스턴스 인지 지정이 안되있다.
    // accept(message); 매서드로 인스턴스를 인자로 넣어주고, 수식을 실행시킨다.
    private void delete(Long id, Consumer<Message> delete) { // 5
        Message message = messageRepository.findById(id).orElseThrow(MessageNotFoundException::new);
        delete.accept(message);
        if(message.isDeletable()) {
            messageRepository.delete(message);
        }
    }
}
