package org.example.basicMarket.repository.post;

import org.example.basicMarket.entity.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post,Long> {

    // 각 게시글을 조회할 때는 작성자의 정보도 보내줄 것이기 때문에, fetch join을 이용하여 Member도 함꼐 조회해준다. 연관된 Member table 정보도 동시에 함께 불러온다.(1개의 쿼리만 나간다)
    // join만 쓴다면 Post의 정보만 불러올것이다. member가 읽힐때 sql문이 추가되어 읽혀질 것이다.
    @Query("select p from Post p join fetch p.member where p.id = :id")
    Optional<Post> findByIdWithMember(Long id);
}
