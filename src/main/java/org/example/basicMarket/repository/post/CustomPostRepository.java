package org.example.basicMarket.repository.post;

import org.example.basicMarket.dto.post.PostReadCondition;
import org.example.basicMarket.dto.post.PostSimpleDto;
import org.springframework.data.domain.Page;

// page 객체 : spring data JPA가 제공하는 페이징 및 정렬 기능을 활용
// simple(쩝두어) : Post의 일부필드만을 가지고 있는 데이터 전송 객체(DTO:data transfer Object)이다.
// 이 interface는 PostRepository가 상속받아서, PostRepository가 findALlByCondition 매서드를 사용할 수 있게 된다.
public interface CustomPostRepository {
Page<PostSimpleDto> findAllByCondition(PostReadCondition cond);
}
