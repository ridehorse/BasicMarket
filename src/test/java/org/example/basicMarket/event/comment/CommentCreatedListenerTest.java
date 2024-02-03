package org.example.basicMarket.event.comment;

import org.example.basicMarket.dto.alarm.AlarmInfoDto;
import org.example.basicMarket.dto.member.MemberDto;
import org.example.basicMarket.event.commment.CommentCreatedEvent;
import org.example.basicMarket.service.alarm.AlarmService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.transaction.annotation.Transactional;

import static org.example.basicMarket.factory.entity.MemberFactory.createMemberWithId;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest // 통합테스트 진행(발행된 이벤트를 리스너가 처리하는 과정을 검증하기 위해)
@ActiveProfiles(value = "test")
@Transactional // 2
@Commit // 테스트에서 @Transcational은 기본적으로 롤백을 하게 된다. 하지만 @TransactionalEventListener은 트렌젝션이 커밋되고 동작하므로, 강제로 커밋을 내려준다.
class CommentCreatedListenerTest {
    @Autowired
    ApplicationEventPublisher publisher;
    @MockBean(name = "smsAlarmService") // @MockBean : @SpringBootTest에서 사용 .. 실제 외부서비스 호출 대신에 모킹된(가자) 빈을 주입
    AlarmService smsAlarmService; // smsAlarmService 이름을 가진 AlarmService type의 빈을 주입한다.
    @MockBean(name = "emailAlarmService") AlarmService emailAlarmService; // 4
    @MockBean(name = "lineAlarmService") AlarmService lineAlarmService; // 4

    int calledCount;

    @AfterTransaction
        // 5
    void afterEach() {
        verify(emailAlarmService, times(calledCount)).alarm(any(AlarmInfoDto.class));
        verify(lineAlarmService, times(calledCount)).alarm(any(AlarmInfoDto.class));
        verify(smsAlarmService, times(calledCount)).alarm(any(AlarmInfoDto.class));
    }

    @Test
    void handleCommentCreatedEventTest() {
        // given
        MemberDto publisher = MemberDto.toDto(createMemberWithId(1L));
        MemberDto postWriter = MemberDto.toDto(createMemberWithId(2L));
        MemberDto parentWriter = MemberDto.toDto(createMemberWithId(3L));
        String content = "content";

        // when
        this.publisher.publishEvent(new CommentCreatedEvent(publisher, postWriter, parentWriter, content));

        //then
        calledCount = 2;
    }

    @Test
    void handleCommentCreatedEventWhenPublisherIsPostWriterTest() {
        // given
        MemberDto publisher = MemberDto.toDto(createMemberWithId(1L));
        MemberDto postWriter = MemberDto.toDto(createMemberWithId(1L));
        MemberDto parentWriter = MemberDto.empty();
        String content = "content";

        // when
        this.publisher.publishEvent(new CommentCreatedEvent(publisher, postWriter, parentWriter, content));

        // then
        calledCount = 0;
    }

    @Test
    void handleCommentCreatedEventWhenPublisherIsParentWriterTest() {
        // given
        MemberDto publisher = MemberDto.toDto(createMemberWithId(1L));
        MemberDto postWriter = MemberDto.toDto(createMemberWithId(2L));
        MemberDto parentWriter = MemberDto.toDto(createMemberWithId(1L));
        String content = "content";

        // when
        this.publisher.publishEvent(new CommentCreatedEvent(publisher, postWriter, parentWriter, content));

        // then
        calledCount = 1;
    }

    @Test
    void handleCommentCreatedEventWhenPostWriterIsParentWriterTest() {
        // given
        MemberDto publisher = MemberDto.toDto(createMemberWithId(1L));
        MemberDto postWriter = MemberDto.toDto(createMemberWithId(2L));
        MemberDto parentWriter = MemberDto.toDto(createMemberWithId(2L));
        String content = "content";

        // when
        this.publisher.publishEvent(new CommentCreatedEvent(publisher, postWriter, parentWriter, content));

        // then
        calledCount = 1;
    }
}
