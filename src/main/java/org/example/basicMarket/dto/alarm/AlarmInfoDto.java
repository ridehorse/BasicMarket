package org.example.basicMarket.dto.alarm;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.basicMarket.dto.member.MemberDto;

@Data
@AllArgsConstructor
public class AlarmInfoDto {

    private MemberDto target;
    private String message;

}
