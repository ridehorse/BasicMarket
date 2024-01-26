package org.example.basicMarket.dto.post;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.basicMarket.dto.member.MemberDto;
import org.example.basicMarket.entity.post.Post;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Data
@AllArgsConstructor
public class PostDto {

    private Long id;
    private String title;
    private String content;
    private Long price;
    private MemberDto member;
    private List<ImageDto> images;

    // 응답 바디로 전송하기 위해 JSON 으로 변환될 때, @JsonFormat으로 지정해둔 형태로 LocalDateTime이 변환되어 응답
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime modifiedAt;

    public static PostDto toDto(Post post){

        return new PostDto(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getPrice(),
                MemberDto.toDto(post.getMember()),
                post.getImages().stream().map(i->ImageDto.toDto(i)).collect(toList()), //원래 파일과 서버에서 생성한 고유한 파일명으로 DTO를 생성해서 반환
                post.getCreateAt(),
                post.getModifiedAt()
        );
    }

}
