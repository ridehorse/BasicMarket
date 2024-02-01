package org.example.basicMarket.dto.message;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.basicMarket.dto.member.MemberDto;
import org.example.basicMarket.entity.message.Message;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class MessageDto {
    private Long id;
    private String content;
    private MemberDto sender;
    private MemberDto receiver;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;

    public static MessageDto toDto(Message message) {
        return new MessageDto(
                message.getId(),
                message.getContent(),
                MemberDto.toDto(message.getSender()),
                MemberDto.toDto(message.getReceiver()),
                message.getCreatedAt());
    }
}
