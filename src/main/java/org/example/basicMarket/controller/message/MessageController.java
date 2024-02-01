package org.example.basicMarket.controller.message;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.basicMarket.aop.AssignMemberId;
import org.example.basicMarket.dto.message.MessageCreateRequest;
import org.example.basicMarket.dto.message.MessageReadCondition;
import org.example.basicMarket.dto.response.Response;
import org.example.basicMarket.service.message.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Api(value = "Message Controller", tags = "Message")
@RestController
@RequiredArgsConstructor
@Slf4j
public class MessageController {
    private final MessageService messageService;

    //  MessageReadCondition
    //    private Long memberId;
    //    private Long lastMessageId = Long.MAX_VALUE;
    //    private Integer size;

    // MessageListDto
    //    private int numberOfElements;
    //    private boolean hasNext;
    //    private List<MessageSimpleDto> messageList;
    //    public static MessageListDto toDto(Slice<MessageSimpleDto> slice) {
    //        return new MessageListDto(slice.getNumberOfElements(), slice.hasNext(), slice.getContent());
    //    }
    @ApiOperation(value = "송신자의 쪽지 목록 조회", notes = "송신자의 쪽지 목록을 조회한다.")
    @GetMapping("/api/messages/sender")
    @ResponseStatus(HttpStatus.OK)
    @AssignMemberId
    public Response readAllBySender(@Valid MessageReadCondition cond) {
        return Response.success(messageService.readAllBySender(cond)); //return MessageListDto
    }

    @ApiOperation(value = "수신자의 쪽지 목록 조회", notes = "수신자의 쪽지 목록을 조회한다.")
    @GetMapping("/api/messages/receiver")
    @ResponseStatus(HttpStatus.OK)
    @AssignMemberId
    public Response readAllByReceiver(@Valid MessageReadCondition cond) {
        return Response.success(messageService.readAllByReceiver(cond));
    }

    @ApiOperation(value = "쪽지 조회", notes = "쪽지를 조회한다.")
    @GetMapping("/api/messages/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Response read(@ApiParam(value = "쪽지 id", required = true) @PathVariable Long id) {
        return Response.success(messageService.read(id));
    }

    // MessageCreateRequest
    //    private String content;
    //    private Long memberId;
    //    private Long receiverId;
    //    public static Message toEntity(MessageCreateRequest req, MemberRepository memberRepository) {
    //        return new Message(
    //                req.content,
    //                memberRepository.findById(req.memberId).orElseThrow(MemberNotFoundException::new),
    //                memberRepository.findById(req.receiverId).orElseThrow(MemberNotFoundException::new)
    //        );

    @ApiOperation(value = "쪽지 생성", notes = "쪽지를 생성한다.")
    @PostMapping("/api/messages")
    @ResponseStatus(HttpStatus.CREATED)
    @AssignMemberId
    public Response create(@Valid @RequestBody MessageCreateRequest req) {
        messageService.create(req);
        return Response.success();
    }

    @ApiOperation(value = "송신자의 쪽지 삭제", notes = "송신자의 쪽지를 삭제한다.")
    @DeleteMapping("/api/messages/sender/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Response deleteBySender(@ApiParam(value = "쪽지 id", required = true) @PathVariable Long id) {
        messageService.deleteBySender(id);
        return Response.success();
    }

    @ApiOperation(value = "수신자의 쪽지 삭제", notes = "수신자의 쪽지를 삭제한다.")
    @DeleteMapping("/api/messages/receiver/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Response deleteByReceiver(@ApiParam(value = "쪽지 id", required = true) @PathVariable Long id) {
        messageService.deleteByReceiver(id);
        return Response.success();
    }
}