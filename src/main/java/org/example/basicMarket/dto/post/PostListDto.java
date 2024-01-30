package org.example.basicMarket.dto.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@AllArgsConstructor
public class PostListDto {
    private Long totalElements;
    private Integer totalPages;
    private boolean hasNext;
    private List<PostSimpleDto> postList;

    //repository에서 paging정보가 적용이된 Page<PostSimpleDto> 객체를 인자로 받는다.
    public static PostListDto toDto(Page<PostSimpleDto> page) {
        // 총 게시글 개수, 총 페이지 수, 다음 페이지가 있는지, 실제 페이지 내역(우리 코드에서는 List<PostSimpleDto> 객체이다.)
        return new PostListDto(page.getTotalElements(), page.getTotalPages(), page.hasNext(), page.getContent());
    }
}
