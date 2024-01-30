package org.example.basicMarket.controller.comment;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.basicMarket.aop.AssignMemberId;
import org.example.basicMarket.dto.comment.CommentCreateRequest;
import org.example.basicMarket.dto.comment.CommentReadCondition;
import org.example.basicMarket.dto.response.Response;
import org.example.basicMarket.service.comment.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Api(value = "Comment Controller", tags = "Comment")
@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @ApiOperation(value = "댓글 목록 조회", notes = "댓글 목록을 조회한다.")
    @GetMapping("/api/comments")
    @ResponseStatus(HttpStatus.OK)
    public Response readAll(@Valid CommentReadCondition cond) { // private Long postId;
        return Response.success(commentService.readAll(cond));
    }

    //    private String content;
    //    private Long postId;
    //    private Long memberId;
    //    private Long parentId;
    @ApiOperation(value = "댓글 생성", notes = "댓글을 생성한다.")
    @PostMapping("/api/comments")
    @ResponseStatus(HttpStatus.CREATED)
    @AssignMemberId
    public Response create(@Valid @RequestBody CommentCreateRequest req) {
        commentService.create(req);
        return Response.success();
    }

    @ApiOperation(value = "댓글 삭제", notes = "댓글을 삭제한다.")
    @DeleteMapping("/api/comments/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Response delete(@ApiParam(value = "댓글 id", required = true) @PathVariable Long id) {
        commentService.delete(id);
        return Response.success();
    }

}
