package org.example.basicMarket.service.alarm;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.example.basicMarket.dto.alarm.AlarmInfoDto;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SmsAlarmService implements AlarmService{

    @Override
    public void alarm(AlarmInfoDto infoDto) {
        log.info("{} 에게 문자메시지 전송 = {}", infoDto.getTarget().getUsername(), infoDto.getMessage());
    }
}
