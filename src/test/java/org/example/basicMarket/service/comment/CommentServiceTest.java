package org.example.basicMarket.service.comment;

import org.example.basicMarket.dto.comment.CommentDto;
import org.example.basicMarket.event.commment.CommentCreatedEvent;
import org.example.basicMarket.exception.CommentNotFoundException;
import org.example.basicMarket.exception.MemberNotFoundException;
import org.example.basicMarket.exception.PostNotFoundException;
import org.example.basicMarket.repository.comment.CommentRepository;
import org.example.basicMarket.repository.member.MemberRepository;
import org.example.basicMarket.repository.post.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.example.basicMarket.factory.dto.CommentCreateRequestFactory.createCommentCreateRequest;
import static org.example.basicMarket.factory.dto.CommentCreateRequestFactory.createCommentCreateRequestWithParentId;
import static org.example.basicMarket.factory.dto.CommentReadConditionFactory.createCommentReadCondition;
import static org.example.basicMarket.factory.entity.CommentFactory.createComment;
import static org.example.basicMarket.factory.entity.CommentFactory.createDeletedComment;
import static org.example.basicMarket.factory.entity.MemberFactory.createMember;
import static org.example.basicMarket.factory.entity.PostFactory.createPost;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @InjectMocks
    CommentService commentService;
    @Mock
    CommentRepository commentRepository;
    @Mock
    MemberRepository memberRepository;
    @Mock
    PostRepository postRepository;

    @Mock
    ApplicationEventPublisher publisher;

    @Test
    void readAllTest() {
        // given
        given(commentRepository.findAllWithMemberAndParentByPostIdOrderByParentIdAscNullsFirstCommentIdAsc(anyLong()))
                .willReturn(
                        List.of(createComment(null),
                                createComment(null)
                        )
                );

        // when
        List<CommentDto> result = commentService.readAll(createCommentReadCondition());

        // then
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    void readAllDeletedCommentTest() {
        // given
        given(commentRepository.findAllWithMemberAndParentByPostIdOrderByParentIdAscNullsFirstCommentIdAsc(anyLong()))
                .willReturn(
                        List.of(createDeletedComment(null),
                                createDeletedComment(null)
                        )
                );

        // when
        List<CommentDto> result = commentService.readAll(createCommentReadCondition());

        // then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getContent()).isNull();
        assertThat(result.get(0).getMember()).isNull();
    }

    @Test
    void createTest() {
        // given
        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);

        given(memberRepository.findById(anyLong())).willReturn(Optional.of(createMember()));
        given(postRepository.findById(anyLong())).willReturn(Optional.of(createPost()));
        given(commentRepository.save(any())).willReturn(createComment(null));

        // when
        commentService.create(createCommentCreateRequest());

        // then
        verify(commentRepository).save(any());
        // publisher 필드에 담긴 객체의 publishEvent 매서드가 특정한 인자로 호출되었는지를 검증
        // verify(publisher) : publisher 객체의 메서드 호출을 확인
        // publishEvent() : publishEvent() 매서드가 호출 되었는지 검증
        // eventCaptor.capture() : 호출된 메서드에 전달된 인자를 캡쳐(실제로 publishEvent가 호출될때 전달된 인자를 저장한다.)
        verify(publisher).publishEvent(eventCaptor.capture());

        Object event = eventCaptor.getValue();
        assertThat(event).isInstanceOf(CommentCreatedEvent.class);
    }

    @Test
    void createExceptionByMemberNotFoundTest() {
        // given
        given(memberRepository.findById(anyLong())).willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> commentService.create(createCommentCreateRequest()))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void createExceptionByPostNotFoundTest() {
        // given
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(createMember()));
        given(postRepository.findById(anyLong())).willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> commentService.create(createCommentCreateRequest()))
                .isInstanceOf(PostNotFoundException.class);
    }

    @Test
    void createExceptionByCommentNotFoundTest() {
        // given
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(createMember()));
        given(postRepository.findById(anyLong())).willReturn(Optional.of(createPost()));
        given(commentRepository.findById(anyLong())).willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> commentService.create(createCommentCreateRequestWithParentId(1L)))
                .isInstanceOf(CommentNotFoundException.class);
    }
}
