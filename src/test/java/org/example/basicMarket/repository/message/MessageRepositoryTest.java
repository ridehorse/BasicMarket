package org.example.basicMarket.repository.message;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.example.basicMarket.config.QuerydslConfig;
import org.example.basicMarket.dto.message.MessageSimpleDto;
import org.example.basicMarket.entity.member.Member;
import org.example.basicMarket.entity.message.Message;
import org.example.basicMarket.exception.MessageNotFoundException;
import org.example.basicMarket.factory.entity.MemberFactory;
import org.example.basicMarket.repository.member.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.example.basicMarket.factory.entity.MessageFactory.createMessage;

@DataJpaTest
@Import(QuerydslConfig.class)
class MessageRepositoryTest {
    @Autowired
    MessageRepository messageRepository;
    @Autowired
    MemberRepository memberRepository;
    @PersistenceContext
    EntityManager em;

    Member sender, receiver;

    @BeforeEach
    void beforeEach() {
        sender = memberRepository.save(MemberFactory.createMember("sender@sender.com", "sender", "sender", "sender"));
        receiver = memberRepository.save(MemberFactory.createMember("receiver@receiver.com", "receiver", "receiver", "receiver"));
    }

    @Test
    void createAndReadTest() {
        // given
        Message message = messageRepository.save(createMessage(sender, receiver));
        clear();

        // when
        Message foundMessage = messageRepository.findById(message.getId()).orElseThrow(MessageNotFoundException::new);

        // then
        assertThat(foundMessage.getId()).isEqualTo(message.getId());
    }

    @Test
    void deleteTest() {
        // given
        Message message = messageRepository.save(createMessage(sender, receiver));

        // when
        messageRepository.delete(message);

        // then
        assertThat(messageRepository.findById(message.getId())).isEmpty();
    }

    @Test
    void deleteCascadeBySenderTest() {
        // given
        Message message = messageRepository.save(createMessage(sender, receiver));
        clear();

        // when
        memberRepository.deleteById(sender.getId());
        clear();

        // then
        assertThat(messageRepository.findById(message.getId())).isEmpty();
    }

    @Test
    void deleteCascadeByReceiverTest() {
        // given
        Message message = messageRepository.save(createMessage(sender, receiver));
        clear();

        // when
        memberRepository.deleteById(receiver.getId());
        clear();

        // then
        assertThat(messageRepository.findById(message.getId())).isEmpty();
    }

    @Test
    void findWithSenderAndReceiverByIdTest() {
        // given
        Message message = messageRepository.save(createMessage(sender, receiver));
        clear();

        // when
        Message foundMessage = messageRepository.findWithSenderAndReceiverById(message.getId()).orElseThrow(MessageNotFoundException::new);

        // then
        assertThat(foundMessage.getId()).isEqualTo(message.getId());
        assertThat(foundMessage.getSender().getEmail()).isEqualTo(sender.getEmail());
        assertThat(foundMessage.getReceiver().getEmail()).isEqualTo(receiver.getEmail());
    }

    @Test
    void findAllBySenderIdOrderByMessageIdDescTest() {
        // given
        List<Message> messages = IntStream.range(0, 4)
                .mapToObj(i -> messageRepository.save(createMessage(sender, receiver))).collect(toList());
        messages.get(2).deleteBySender();
        final int size = 2;
        clear();

        // when
        Slice<MessageSimpleDto> result1 = messageRepository.findAllBySenderIdOrderByMessageIdDesc(sender.getId(), Long.MAX_VALUE, Pageable.ofSize(size));
        List<MessageSimpleDto> content1 = result1.getContent();
        Long lastMessageId1 = content1.get(content1.size() - 1).getId();

        Slice<MessageSimpleDto> result2 = messageRepository.findAllBySenderIdOrderByMessageIdDesc(sender.getId(), lastMessageId1, Pageable.ofSize(size));
        List<MessageSimpleDto> content2 = result2.getContent();

        // then
        assertThat(result1.hasNext()).isTrue();
        assertThat(result1.getNumberOfElements()).isEqualTo(2);
        assertThat(content1.get(0).getId()).isEqualTo(messages.get(3).getId());
        assertThat(content1.get(1).getId()).isEqualTo(messages.get(1).getId());

        assertThat(result2.hasNext()).isFalse();
        assertThat(result2.getNumberOfElements()).isEqualTo(1);
        assertThat(content2.get(0).getId()).isEqualTo(messages.get(0).getId());
    }

    @Test
    void findAllByReceiverIdOrderByMessageIdDescTest() {
        // given
        List<Message> messages = IntStream.range(0, 4)
                .mapToObj(i -> messageRepository.save(createMessage(sender, receiver))).collect(toList());
        messages.get(2).deleteByReceiver();
        final int size = 2;
        clear();

        // when
        // id 1,2,3,4 message 4개 생성
        // id 1,3,4 (2는 삭제) -> messages.get(0) = 1, messages.get(1) = 2,messages.get(2) = 3,messages.get(3) = 4
        // Long이 가지는 가장큰 정수보다 작은 id 값의 메세지 모두 호출 후 size:2(2개) 짜리만 Slice객체에 담긴다. -> result1.getContent().size() = 2 (현재페이지의 객체만 담김, 2개)
        // 뒤에서(desc) 2개 가지고 오므로, (4,3)순서로 담겨 있다.
        // content1.get(0) = 4
        // contetn1.get(1) = 3
        Slice<MessageSimpleDto> result1 = messageRepository.findAllByReceiverIdOrderByMessageIdDesc(receiver.getId(), Long.MAX_VALUE, Pageable.ofSize(size));
        List<MessageSimpleDto> content1 = result1.getContent();
        Long lastMessageId1 = content1.get(content1.size() - 1).getId();

        Slice<MessageSimpleDto> result2 = messageRepository.findAllByReceiverIdOrderByMessageIdDesc(receiver.getId(), lastMessageId1, Pageable.ofSize(size));
        List<MessageSimpleDto> content2 = result2.getContent();

        // then
        assertThat(result1.hasNext()).isTrue();
        assertThat(result1.getNumberOfElements()).isEqualTo(2);
        assertThat(content1.get(0).getId()).isEqualTo(messages.get(3).getId());
        assertThat(content1.get(1).getId()).isEqualTo(messages.get(1).getId());

        assertThat(result2.hasNext()).isFalse();
        assertThat(result2.getNumberOfElements()).isEqualTo(1);
        assertThat(content2.get(0).getId()).isEqualTo(messages.get(0).getId());
    }

    void clear() {
        em.flush();
        em.clear();
    }
}
