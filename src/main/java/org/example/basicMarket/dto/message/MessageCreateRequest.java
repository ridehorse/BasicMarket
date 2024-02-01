package org.example.basicMarket.dto.message;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.basicMarket.entity.message.Message;
import org.example.basicMarket.exception.MemberNotFoundException;
import org.example.basicMarket.repository.member.MemberRepository;

@ApiModel(value = "쪽지 생성 요청")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageCreateRequest {
    @ApiModelProperty(value = "쪽지", notes = "쪽지를 입력해주세요", required = true, example = "my message")
    @NotBlank(message = "쪽지를 입력해주세요.")
    private String content;

    @ApiModelProperty(hidden = true)
    @Null
    private Long memberId;

    @ApiModelProperty(value = "수신자 아이디", notes = "수신자 아이디를 입력해주세요", example = "7")
    @NotNull(message = "수신자 아이디를 입력해주세요.")
    @Positive(message = "올바른 수신자 아이디를 입력해주세요.")
    private Long receiverId;

    public static Message toEntity(MessageCreateRequest req, MemberRepository memberRepository) {
        return new Message(
                req.content,
                memberRepository.findById(req.memberId).orElseThrow(MemberNotFoundException::new),
                memberRepository.findById(req.receiverId).orElseThrow(MemberNotFoundException::new)
        );
    }
}
