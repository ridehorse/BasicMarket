package org.example.basicMarket.entity.comment;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.basicMarket.dto.member.MemberDto;
import org.example.basicMarket.entity.common.EntityDate;
import org.example.basicMarket.entity.member.Member;
import org.example.basicMarket.entity.post.Post;
import org.example.basicMarket.event.commment.CommentCreatedEvent;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.context.ApplicationEventPublisher;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class Comment extends EntityDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Lob
    private String content;

    @Column(nullable = false)
    private boolean deleted; // 1

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member; // 2

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Post post; // 3

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Comment parent; // 4

    // 1 <- 2 <- 4
    // this객체가 2번 객체라고 한다면 children필드에는 4번객체가 저장된다.
    // children 필드에 저장된 4번 객체도 comment이므로 자기자신만의 parent 객체가 있을 것이다.
    @OneToMany(mappedBy = "parent")
    private List<Comment> children = new ArrayList<>(); // 5

    public Comment(String content, Member member, Post post, Comment parent) {
        this.content = content;
        this.member = member;
        this.post = post;
        this.parent = parent;
        this.deleted = false;
    }

    // comment 객체(this)가 삭제요청 -> 지울수 있는 코맨트인가? -> 자식존재 확인 -> 없다면 삭제가능 -> 같이 삭제될 부모객체가 존재하는지 확인
    public Optional<Comment> findDeletableComment() { // 6
        return hasChildren() ? Optional.empty() : Optional.of(findDeletableCommentByParent());
    }

    public void delete() { // 7
        this.deleted = true;
    }

    // 계속해서 삭제될수 있는(deleted = true) comment 객체를 찾아 올라간다. 맨 위의 댓글을 삭제하면 OnDeleteAction.CASCADE 조건에 의해
    // 자식 댓글들은 모두 삭제된다.
    private Comment findDeletableCommentByParent() { // 8
        if(isDeletedParent()){
            Comment deletableParent = getParent().findDeletableCommentByParent();
            if(getParent().getChildren().size() == 1) return deletableParent;
        }
        return this;
//        return isDeletableParent() ? getParent().findDeletableCommentByParent() : this;
    }

    private boolean hasChildren() { // 9
        return getChildren().size() != 0;
    }

    // 지금 삭제를 요청한 자기자신 comment 객체가 자식이 없기 때문에 삭제 할수 있다는것을 알았다.
    // 그런데 자신을 삭제하기 전에 자신때문에 이미 논리적으로 삭제는 되있지만 자식객체때문에 삭제가 안된 부모객체를 찾아야 한다.
    // 진짜 DB상에서 삭제가 가능한 부모댓글 객체의 조건 -> deleted = true , 부모의 자식갯수가 1개(삭제요청한 자기자신 comment)
    // 이걸 만족하면 자기자신의 부모댓글 객체를 삭제한다.
//    private boolean isDeletableParent() { // 10
//        return getParent() != null && getParent().isDeleted() && getParent().getChildren().size() == 1;
//    }

    private boolean isDeletedParent(){
        return getParent() != null && getParent().isDeleted();
    }

    public void publishCreatedEvent(ApplicationEventPublisher publisher) {
        publisher.publishEvent(
                new CommentCreatedEvent(
                        MemberDto.toDto(getMember()),
                        MemberDto.toDto(getPost().getMember()),
                        Optional.ofNullable(getParent()).map(p -> p.getMember()).map(m -> MemberDto.toDto(m)).orElseGet(() -> MemberDto.empty()),
                        getContent()
                )
        );
    }
}
