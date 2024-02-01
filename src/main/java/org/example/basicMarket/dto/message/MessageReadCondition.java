package org.example.basicMarket.dto.message;

import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageReadCondition {

    @ApiModelProperty(hidden = true)
    @Null
    private Long memberId;

    @ApiModelProperty(value = "마지막 쪽지 id", notes = "조회된 쪽지의 마지막 id를 입력해주세요.", required = true, example = "7")
    private Long lastMessageId = Long.MAX_VALUE;

    @ApiModelProperty(value = "페이지 크기", notes = "페이지 크기를 입력해주세요", required = true, example = "10")
    @NotNull(message = "페이지 크기를 입력해주세요.")
    @Positive(message = "올바른 페이지 크기를 입력해주세요. (1 이상)")
    private Integer size;
}
