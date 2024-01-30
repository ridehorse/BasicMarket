package org.example.basicMarket.entity.comment;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.basicMarket.factory.entity.CommentFactory.createComment;

// entity 테스트에 @DataJpaTest를 안하는 이유는 child parent 객체 접근시에 모두 DB접속이 아니라 자바 객체(getter,생성자)로만 테스트 하기 떄문이다.
public class CommentTest {

    @Test
    void deleteTest() {
        // given
        Comment comment = createComment(null); // 부모 댓글 없는 최상위 댓글 생성
        boolean beforeDeleted = comment.isDeleted(); // false 반환

        // when
        comment.delete(); // deleted = true

        // then
        boolean afterDeleted = comment.isDeleted();
        assertThat(beforeDeleted).isFalse();
        assertThat(afterDeleted).isTrue();
    }


    @Test
    void findDeletableCommentWhenExistsTest() {
        // given

        // root 1
        // 1 -> 2
        // 2(del) -> 3(del)
        // 2(del) -> 4
        // 3(del) -> 5
        Comment comment1 = createComment(null); // 최상위 댓글
        Comment comment2 = createComment(comment1);
        Comment comment3 = createComment(comment2);
        Comment comment4 = createComment(comment2);
        Comment comment5 = createComment(comment3);
        comment2.delete();
        comment3.delete();
        ReflectionTestUtils.setField(comment1, "children", List.of(comment2)); // comment1 객체의 children 필드값을 초기화한다.
        ReflectionTestUtils.setField(comment2, "children", List.of(comment3, comment4));
        ReflectionTestUtils.setField(comment3, "children", List.of(comment5));
        ReflectionTestUtils.setField(comment4, "children", List.of());
        ReflectionTestUtils.setField(comment5, "children", List.of());

        // 5번 객체를 지운다면 부모객체는 3번에서 지울수 있다. 2번 부모는 4번 이라는 살아있는 자식이 있어서 지울수 없다.
        // when
        Optional<Comment> deletableComment = comment5.findDeletableComment();

        // then
        assertThat(deletableComment).containsSame(comment3);
    }

    @Test
    void findDeletableCommentWhenNotExistsTest() {
        // given

        // root 1
        // 1 -> 2
        // 2 -> 3
        Comment comment1 = createComment(null);
        Comment comment2 = createComment(comment1);
        Comment comment3 = createComment(comment2);
        ReflectionTestUtils.setField(comment1, "children", List.of(comment2));
        ReflectionTestUtils.setField(comment2, "children", List.of(comment3));
        ReflectionTestUtils.setField(comment3, "children", List.of());

        // when
        Optional<Comment> deletableComment = comment2.findDeletableComment();

        // then
        assertThat(deletableComment).isEmpty();
    }
}
