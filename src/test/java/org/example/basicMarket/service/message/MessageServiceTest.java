package org.example.basicMarket.service.message;

import org.example.basicMarket.dto.message.MessageCreateRequest;
import org.example.basicMarket.dto.message.MessageDto;
import org.example.basicMarket.dto.message.MessageListDto;
import org.example.basicMarket.dto.message.MessageReadCondition;
import org.example.basicMarket.entity.message.Message;
import org.example.basicMarket.exception.MemberNotFoundException;
import org.example.basicMarket.exception.MessageNotFoundException;
import org.example.basicMarket.repository.member.MemberRepository;
import org.example.basicMarket.repository.message.MessageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.example.basicMarket.factory.entity.MemberFactory.createMember;
import static org.example.basicMarket.factory.entity.MessageCreateRequestFactory.createMessageCreateRequest;
import static org.example.basicMarket.factory.entity.MessageFactory.createMessage;
import static org.example.basicMarket.factory.entity.MessageReadConditionFactory.createMessageReadCondition;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {
    @InjectMocks
    MessageService messageService;
    @Mock
    MessageRepository messageRepository;
    @Mock
    MemberRepository memberRepository;

    @Test
    void readAllBySenderTest() {
        // given
        MessageReadCondition cond = createMessageReadCondition();
        given(messageRepository.findAllBySenderIdOrderByMessageIdDesc(anyLong(), anyLong(), any(Pageable.class)))
                .willReturn(new SliceImpl<>(List.of(), Pageable.ofSize(2), false));

        // when
        MessageListDto result = messageService.readAllBySender(cond);

        // then
        assertThat(result.getNumberOfElements()).isZero();
        assertThat(result.getMessageList().size()).isZero();
        assertThat(result.isHasNext()).isFalse();
    }

    @Test
    void readAllByReceiverTest() {
        // given
        MessageReadCondition cond = createMessageReadCondition();
        given(messageRepository.findAllByReceiverIdOrderByMessageIdDesc(anyLong(), anyLong(), any(Pageable.class)))
                .willReturn(new SliceImpl<>(List.of(), Pageable.ofSize(2), false));

        // when
        MessageListDto result = messageService.readAllByReceiver(cond);

        // then
        assertThat(result.getNumberOfElements()).isZero();
        assertThat(result.getMessageList().size()).isZero();
        assertThat(result.isHasNext()).isFalse();
    }

    @Test
    void readTest() {
        // given
        Long id = 1L;
        Message message = createMessage();
        given(messageRepository.findWithSenderAndReceiverById(id)).willReturn(Optional.of(message));

        // when
        MessageDto result = messageService.read(id);

        // then
        assertThat(result.getContent()).isEqualTo(message.getContent());
    }

    @Test
    void readExceptionByMessageNotFoundTest() {
        // given
        Long id = 1L;
        given(messageRepository.findWithSenderAndReceiverById(id)).willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> messageService.read(id)).isInstanceOf(MessageNotFoundException.class);
    }

    @Test
    void createTest() {
        // given
        MessageCreateRequest req = createMessageCreateRequest();
        given(memberRepository.findById(req.getMemberId())).willReturn(Optional.of(createMember()));
        given(memberRepository.findById(req.getReceiverId())).willReturn(Optional.of(createMember()));

        // when
        messageService.create(req);

        // then
        verify(messageRepository).save(any());
    }

    @Test
    void createExceptionBySenderNotFoundTest() {
        // given
        MessageCreateRequest req = createMessageCreateRequest();
        given(memberRepository.findById(req.getMemberId())).willReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> messageService.create(req)).isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void createExceptionByReceiverNotFoundTest() {
        // given
        MessageCreateRequest req = createMessageCreateRequest();
        given(memberRepository.findById(req.getMemberId())).willReturn(Optional.of(createMember()));
        given(memberRepository.findById(req.getReceiverId())).willReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> messageService.create(req)).isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void deleteBySenderNotDeletableTest() {
        // given
        Long id = 1L;
        Message message = createMessage();
        given(messageRepository.findById(id)).willReturn(Optional.of(message));

        // when
        messageService.deleteBySender(id);

        // then
        assertThat(message.isDeletedBySender()).isTrue();
        verify(messageRepository, never()).delete(any(Message.class));
    }

    @Test
    void deleteBySenderDeletableByAlreadyReceiverDeletionTest() {
        // given
        Long id = 1L;
        Message message = createMessage();
        message.deleteByReceiver();
        given(messageRepository.findById(id)).willReturn(Optional.of(message));

        // when
        messageService.deleteBySender(id);

        // then
        assertThat(message.isDeletedBySender()).isTrue();
        verify(messageRepository).delete(any(Message.class));
    }

    @Test
    void deleteBySenderExceptionByMessageNotFoundTest() {
        // given
        Long id = 1L;
        given(messageRepository.findById(id)).willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> messageService.deleteBySender(id)).isInstanceOf(MessageNotFoundException.class);
    }

    @Test
    void deleteByReceiverNotDeletableTest() {
        // given
        Long id = 1L;
        Message message = createMessage();
        given(messageRepository.findById(id)).willReturn(Optional.of(message));

        // when
        messageService.deleteByReceiver(id);

        // then
        assertThat(message.isDeletedByReceiver()).isTrue();
        verify(messageRepository, never()).delete(any(Message.class));
    }

    @Test
    void deleteByReceiverDeletableByAlreadySenderDeletionTest() {
        // given
        Long id = 1L;
        Message message = createMessage();
        message.deleteBySender();
        given(messageRepository.findById(id)).willReturn(Optional.of(message));

        // when
        messageService.deleteByReceiver(id);

        // then
        assertThat(message.isDeletedByReceiver()).isTrue();
        verify(messageRepository).delete(any(Message.class));
    }

    @Test
    void deleteByReceiverExceptionByMessageNotFoundTest() {
        // given
        Long id = 1L;
        given(messageRepository.findById(id)).willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> messageService.deleteByReceiver(id)).isInstanceOf(MessageNotFoundException.class);
    }
}