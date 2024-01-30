package org.example.basicMarket.repository.comment;

import org.example.basicMarket.entity.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment,Long> {

    // c.parent : comment 객체의 parent 필드를 나타낸다.
    // left join : comment와 parent의 조인이므로 당연히 parent_id와 id가 같은 조건일것이다(on으로 적지 않아도 된다)
    // c.id = :id : 조인 된 후 만들어진 table에서 comment의 id 필드가 :id인 행을 선택한다.
    @Query("select c from Comment c left join fetch c.parent where c.id = :id")
    Optional<Comment> findWithParentById(@Param("id") Long id);

    //c.parent.id nulls fisrt : parent 객체의 id가 null 이라는것은 parent 객체가 없다는 것이고 이 행의 comment 객체는 최상위 객체이다.
    @Query("select c from Comment c join fetch c.member left join fetch c.parent where c.post.id = :postId order by c.parent.id asc nulls first, c.id asc")
    List<Comment> findAllWithMemberAndParentByPostIdOrderByParentIdAscNullsFirstCommentIdAsc(@Param("postId") Long postId);
}
