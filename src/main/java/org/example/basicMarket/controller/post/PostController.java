package org.example.basicMarket.controller.post;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.basicMarket.aop.AssignMemberId;
import org.example.basicMarket.dto.post.PostCreateRequest;
import org.example.basicMarket.dto.post.PostReadCondition;
import org.example.basicMarket.dto.post.PostUpdateRequest;
import org.example.basicMarket.dto.response.Response;
import org.example.basicMarket.service.post.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Api(value = "Post Controller", tags = "Post")
@RestController
@RequiredArgsConstructor
@Slf4j
public class PostController {
    private final PostService postService;

    //게시글 데이터를 이미지와 함께 전달받을 수 있도록, 요청하는 Content-Type이 multipart/form-data를 이용해야합니다.
    //
    //따라서 PostCreateRequest 파라미터에 @ModelAttribute를 선언해줍니다.
    //@ModelAttribute에 대해 validation 제약 조건이 위배되면, BindException 예외가 발생하게 됩니다.
    //
    //기존에 다른 API에 작성했던 @RequestBody에서는, MethodArgumentNotValidException 예외가 발생하고 있었습니다.
    //BindException은 MethodArgumentNotValidException의 상위 클래스입니다.
    //
    //두 예외는 유사한 상황에 발생하므로, ExceptionAdvice에서 MethodArgumentNotValidException 예외를 잡아내던 것을, BindException 예외를 잡아내도록 수정하여, 두 예외를 모두 잡아낼 수 있도록 하겠습니다.
    @ApiOperation(value = "게시글 생성", notes = "게시글을 생성한다.")
    @PostMapping("/api/posts")
    @ResponseStatus(HttpStatus.CREATED)
    @AssignMemberId // create() 매서드가 실행되게 전에, AOP에서 PostCreateRequest객체의 필드인 memberId의 값을 초기화 한다.(memberId 값은 security의 유저정보를 가지고 잇는 CustomUserDetails 객체에서 가지고 온다)
    public Response create(@Valid @ModelAttribute PostCreateRequest req) {
        return Response.success(postService.create(req));
    }

    @ApiOperation(value = "게시글 조회", notes = "게시글을 조회한다.")
    @GetMapping("/api/posts/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Response read(@ApiParam(value = "게시글 id", required = true) @PathVariable Long id) {
        return Response.success(postService.read(id));
    }

    @ApiOperation(value = "게시글 삭제", notes = "게시글을 삭제한다.")
    @DeleteMapping("/api/posts/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Response delete(@ApiParam(value = "게시글 id", required = true) @PathVariable Long id) {
        postService.delete(id);
        return Response.success();
    }

    @ApiOperation(value = "게시글 수정", notes = "게시글을 수정한다.")
    @PutMapping("/api/posts/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Response update(
            @ApiParam(value = "게시글 id", required = true) @PathVariable Long id,
            @Valid @ModelAttribute PostUpdateRequest req) {
        return Response.success(postService.update(id, req));
    }

    @ApiOperation(value = "게시글 목록 조회", notes = "게시글 목록을 조회한다.")
    @GetMapping("/api/posts")
    @ResponseStatus(HttpStatus.OK)
    public Response readAll(@Valid PostReadCondition cond) {
        return Response.success(postService.readAll(cond));
    }
}
