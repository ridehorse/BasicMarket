package org.example.basicMarket.event.commment;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.basicMarket.dto.alarm.AlarmInfoDto;
import org.example.basicMarket.dto.member.MemberDto;
import org.example.basicMarket.service.alarm.AlarmService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommentCreatedListener {

    private final AlarmService emailAlarmService; // 1
    private final AlarmService lineAlarmService; // 1
    private final AlarmService smsAlarmService; // 1
    private List<AlarmService> alarmServices = new ArrayList<>();

    @PostConstruct // 실행순서 : 기본생성자 -> @Acquired || @RequiredArgsConstructor -> @PostConstruct
    public void postConstruct() { // 2
        alarmServices.add(emailAlarmService);
        alarmServices.add(lineAlarmService);
        alarmServices.add(smsAlarmService);
    }

    @TransactionalEventListener // 3
    @Async // 4
    public void handleAlarm(CommentCreatedEvent event) { // 5
        log.info("CommentCreatedListener.handleAlarm 진입");
        String message = generateAlarmMessage(event);
        if(isAbleToSendToPostWriter(event)) alarmTo(event.getPostWriter(), message);
        if(isAbleToSendToParentWriter(event)) alarmTo(event.getParentWriter(), message);
    }

    private void alarmTo(MemberDto memberDto, String message) { // 6
        alarmServices.stream().forEach(alarmService -> alarmService.alarm(new AlarmInfoDto(memberDto, message)));
    }

    private boolean isAbleToSendToPostWriter(CommentCreatedEvent event) { // 7
        if(!isSameMember(event.getPublisher(), event.getPostWriter())) {
            if(hasParent(event)) return !isSameMember(event.getPostWriter(), event.getParentWriter());
            return true;
        }
        return false;
    }

    private boolean isAbleToSendToParentWriter(CommentCreatedEvent event) { // 8
        return hasParent(event) && !isSameMember(event.getPublisher(), event.getParentWriter());
    }

    private boolean isSameMember(MemberDto a, MemberDto b) {
        return Objects.equals(a.getId(), b.getId());
    }

    private boolean hasParent(CommentCreatedEvent event) {
        return event.getParentWriter().getId() != null;
    }

    private String generateAlarmMessage(CommentCreatedEvent event) { // 9
        return event.getPublisher().getNickname() + " : " + event.getContent();
    }
}
