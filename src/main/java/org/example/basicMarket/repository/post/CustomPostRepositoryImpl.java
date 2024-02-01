package org.example.basicMarket.repository.post;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.example.basicMarket.dto.post.PostReadCondition;
import org.example.basicMarket.dto.post.PostSimpleDto;
import org.example.basicMarket.entity.post.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Function;

import static com.querydsl.core.types.Projections.constructor;
import static org.example.basicMarket.entity.post.QPost.post;
// QuerydslRepositorySupport : 빌드된 쿼리에 손쉽게 페이징을 적용 할 수 있다.
@Transactional(readOnly = true) // QuerydslRepositorySupport를 상속받아 Querydsl을 사용하는 SpringData JPA Repository 구현체임을 나타낸다.
public class CustomPostRepositoryImpl extends QuerydslRepositorySupport implements CustomPostRepository { // 2

    @Autowired
    private final JPAQueryFactory jpaQueryFactory; // 쿼리를 생성하는데 사용되는 객체

    public CustomPostRepositoryImpl(JPAQueryFactory jpaQueryFactory) { // 4
        super(Post.class); //Post.class를 전달하여 QuerydslRepositorySupport는 초기화 한다.
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Page<PostSimpleDto> findAllByCondition(PostReadCondition cond) { // 5
        Pageable pageable = PageRequest.of(cond.getPage(), cond.getSize());
        // Predicate : Querydsl에서 사용되는 조건을 표현하는 인터페이스(검색조건을 포현)
        Predicate predicate = createPredicate(cond);
        // fetchAll(predicate, pageable): 페이징된 결과 데이터를 가져오는 메서드,List<PostSimpleDto> 형태입니다
        // Pageable 객체로, 페이지 번호, 페이지 크기 등의 정보를 포함하고 있습니다. 이 객체는 페이징된 결과를 생성할 때 사용됩니다
        // fetchCount(predicate): 페이징 없이 전체 결과의 카운트를 가져오는 메서드
        return new PageImpl<>(fetchAll(predicate, pageable), pageable, fetchCount(predicate));
    }

    private List<PostSimpleDto> fetchAll(Predicate predicate, Pageable pageable) {
        //select(): Querydsl에서 쿼리의 SELECT 부분을 지정하는 메서드입니다. 여기에 전달되는 파라미터는 결과로 반환될 필드들을 나타냅니다.
        //constructor(...): DTO를 생성하는 특별한 함수입니다. 여기에는 DTO의 클래스와 해당 클래스의 생성자에 전달할 값을 지정합니다.
        //PostSimpleDto.class: DTO 클래스를 지정합니다. 이 경우 PostSimpleDto 클래스를 사용하여 결과를 매핑할 것입니다.
        //post.id, post.title, post.member.nickname, post.createAt: 생성자에 전달될 값들을 지정합니다. 이는 PostSimpleDto의 생성자에 해당하는 필드들을 지정하는 것입니다. 각각의 필드는 post 엔터티에서 가져온 값들로 매핑됩니다.
        //post.id: 게시물의 식별자(ID)
        //post.title: 게시물의 제목
        //post.member.nickname: 게시물 작성자의 닉네임
        //post.createAt: 게시물 작성일시
        //이러한 설정으로 Querydsl은 쿼리의 결과를 PostSimpleDto 객체로 생성하고 해당 객체들을 리스트로 반환합니다. 이를 통해 원하는 필드들만을 갖는 DTO를 사용하여 효과적으로 데이터를 전송하거나 활용할 수 있습니다.
        return getQuerydsl().applyPagination(
                pageable,
                jpaQueryFactory
                        .select(constructor(PostSimpleDto.class, post.id, post.title, post.member.nickname, post.createdAt))
                        .from(post)
                        .join(post.member)
                        .where(predicate)
                        .orderBy(post.id.desc())
        ).fetch(); //반환 형태 : List<PostSimpleDto>
    }

    private Long fetchCount(Predicate predicate) { // 7
        // post.count() post entity의 레코드 수를 가져온다.
        // fetch(): 결과가 여러 개인 경우 리스트로 반환됩니다.
        // fetchFirst(): 결과 중 첫 번째 것만 반환합니다.
        // fetchResults(): QueryResults 객체를 반환하며, 페이징과 관련된 정보를 함께 제공합니다.
        // fetchOne() : 정확히 하나의 결과만을 반환한거나, query의 결과가 여러개라면 NonUniqueResultException 예외 발생
        return jpaQueryFactory.select(post.count()).from(post).where(predicate).fetchOne();
    }

    private Predicate createPredicate(PostReadCondition cond) { // 8
        // BooleanBuilder() : Querydsl에서 사용되는 논리조건을 구성하는 BooleanBuiler 객체를 생성한다. BooleanBuilder는 Predicate를 상속한다.
        // and() : 논리 AND 조건을 추가한다.
        return new BooleanBuilder() // 여기까지는 아무 조건이 없는 BooleanBuilder객체만 생성한다.
                // cond 필드의 객체 : PostReadCondition <Integer page,Integer size,List<Long> categoryId,List<Long> memberId>
                .and(orConditionsByEqCategoryIds(cond.getCategoryId())) // 클라이언트에서 사용자가 검색조건으로 선택한 카테고리 번호들을 List로 담은것을 반환한다.
                .and(orConditionsByEqMemberIds(cond.getMemberId()));
                // 총 반환되는 BooleanBuilder 객체의 형태는 (category_id = 2 or category_id = 3 or category_id = 5) AND (member_id = 3 or member_id = 7 or member_id = 9)
    }

    private Predicate orConditionsByEqCategoryIds(List<Long> categoryIds) { // 9
        // post.category.id::eq : querydsl만의 표현석이다.
        // categoryIds 필드에 담긴 카테고리 번호들(예: 2,3,5 )을 eq(동등비교) 표현식으로 나타낸다, 그리고 각 표현식을 OR 논리로 묶는다. -> category_id = 2 OR category_id = 3 OR category_id = 5 -> 하나의 BooleanBuilder 객체 생성
        return orConditions(categoryIds, post.category.id::eq);
    }

    private Predicate orConditionsByEqMemberIds(List<Long> memberIds) { // 10
        return orConditions(memberIds, post.member.id::eq);
    }

    private <T> Predicate orConditions(List<T> values, Function<T, BooleanExpression> term) { // 11
        return values.stream()
                // term에 담겨진 수식을 map()에 담으면 알아서 apply된다.(value에 담겨진 각각의 요소를 하나하나 eq표현식으로 변환한다. -> stream 요소1 : category_id = 2,stream 요소2 : category_id = 3,stream 요소3 : category_id = 5,
                .map(term)
                // 스트림의 모든 요소를 하나로 줄입니다. or연산을 사용하여 각 요소(조건)을 합친다. -> 하나의 요소 : category_id = 2 or category_id = 3 or category_id = 5
                .reduce(BooleanExpression::or)
                .orElse(null); // 최종적으로 합쳐진 조건을 반환 , 값이 없는 경우 null을 반환 -> BooleanExpression 이나 null 반환
    }
}
