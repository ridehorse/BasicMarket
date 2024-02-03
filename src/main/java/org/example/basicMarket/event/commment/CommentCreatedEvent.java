package org.example.basicMarket.event.commment;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.basicMarket.dto.member.MemberDto;

@Data
@AllArgsConstructor
public class CommentCreatedEvent {

    private MemberDto publisher; //댓글작성자
    private MemberDto postWriter; //게시글 작성자
    private MemberDto parentWriter; // 상위 댓글 작성자
    private String content; // 댓글 내용

}