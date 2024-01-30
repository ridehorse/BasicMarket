package org.example.basicMarket.dto.post;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.basicMarket.entity.post.Image;
import org.example.basicMarket.entity.post.Post;
import org.example.basicMarket.exception.CategoryNotFoundException;
import org.example.basicMarket.exception.MemberNotFoundException;
import org.example.basicMarket.repository.member.CategoryRepository;
import org.example.basicMarket.repository.member.MemberRepository;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@ApiModel(value = "게시글 생성 요청")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateRequest {

    @ApiModelProperty(value = "게시글 제목", notes = "게시글 제목을 입력해주세요", required = true, example = "my title")
    @NotBlank(message = "게시글 제목을 입력해주세요.")
    private String title;

    @ApiModelProperty(value = "게시글 본문", notes = "게시글 본문을 입력해주세요", required = true, example = "my content")
    @NotBlank(message = "게시글 본문을 입력해주세요.")
    private String content;

    @ApiModelProperty(value = "가격", notes = "가격을 입력해주세요", required = true, example = "50000")
    @NotNull(message = "가격을 입력해주세요.")
    @PositiveOrZero(message = "0원 이상을 입력해주세요")
    private Long price;

    //주의할 점은, memberId는 @Null 제약 조건을 가지고 있다는 것입니다.
    //
    //누군가가 작성자를 조작할 수 있으므로, 해당 데이터는 클라이언트에게 전달받지 않겠습니다.
    //
    //대신, AOP를 이용하여 토큰에 저장된 사용자의 ID를 PostCreateRequest에 직접 주입해주도록 하겠습니다.
    @ApiModelProperty(hidden = true)
    @Null
    private Long memberId;

    @ApiModelProperty(value = "카테고리 아이디", notes = "카테고리 아이디를 입력해주세요", required = true, example = "3")
    @NotNull(message = "카테고리 아이디를 입력해주세요.")
    @PositiveOrZero(message = "올바른 카테고리 아이디를 입력해주세요.")
    private Long categoryId;

    //image의 본문 내용은 MultipartFile 객채에 담겨 있다. 클라이언트에서 서버로 요청을 보낼 때 MultipartFile 객체에 담겨서 온다.
    //서버에서 만든 image Entity에는 이미지의 본문 내용이 없다. 단지 오리지널 이름과, 서버에서 새롭게 만든 고유한 이름만 저장될 뿐이다.
    //이미지의 내용은 DB에 저장되지 않는다. 이름만 저장된다. 내용은 controller에서 MultipartFile 객체에 의해 fileSerivce에 의해 지정한 로컬 디렉토리에 바로 저장된다.
    @ApiModelProperty(value = "이미지", notes = "이미지를 첨부해주세요.")
    private List<MultipartFile> images = new ArrayList<>();

    public static Post toEntity(PostCreateRequest req, MemberRepository memberRepository, CategoryRepository categoryRepository) {
        return new Post(
                req.title,
                req.content,
                req.price,
                memberRepository.findById(req.getMemberId()).orElseThrow(MemberNotFoundException::new),
                categoryRepository.findById(req.getCategoryId()).orElseThrow(CategoryNotFoundException::new),
                req.images.stream().map(i -> new Image(i.getOriginalFilename())).collect(toList())
        );
    }
}
