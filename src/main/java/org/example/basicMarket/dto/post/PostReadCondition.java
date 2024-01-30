package org.example.basicMarket.dto.post;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostReadCondition {

    @NotNull(message = "페이지 번호를 입력해주세요.")
    @PositiveOrZero(message = "올바른 페이지 번호를 입력해주세요. (0 이상)")
    private Integer page;

    @NotNull(message = "페이지 크기를 입력해주세요.")
    @Positive(message = "올바른 페이지 크기를 입력해주세요. (1 이상)")
    private Integer size;

    private List<Long> categoryId = new ArrayList<>();
    private List<Long> memberId = new ArrayList<>();
}
