package org.example.basicMarket.repository.post;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.example.basicMarket.config.QuerydslConfig;
import org.example.basicMarket.dto.post.PostReadCondition;
import org.example.basicMarket.dto.post.PostSimpleDto;
import org.example.basicMarket.entity.category.Category;
import org.example.basicMarket.entity.member.Member;
import org.example.basicMarket.entity.post.Post;
import org.example.basicMarket.repository.member.CategoryRepository;
import org.example.basicMarket.repository.member.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.example.basicMarket.factory.dto.PostReadConditionFactory.createPostReadCondition;
import static org.example.basicMarket.factory.entity.CategoryFactory.createCategoryWithName;
import static org.example.basicMarket.factory.entity.MemberFactory.createMember;
import static org.example.basicMarket.factory.entity.PostFactory.createPost;

// @Import(QuerydslConfig.class)는 스프링 테스트에서 사용되는 @DataJpaTest 환경에서 QuerydslConfig 클래스를 가져와서 사용하기 위한 것입니다.
//@DataJpaTest는 JPA 관련 빈들만 등록하기 때문에, QuerydslConfig에서 직접 정의한 JPAQueryFactory 빈이 등록되지 않습니다.
// 따라서 @Import(QuerydslConfig.class)를 사용하여 QuerydslConfig 클래스를 가져와서 필요한 빈들을 등록하도록 설정합니다.
// @Import(QuerydslConfig.class)는 import한 class 내부에 @bean으로 등록된게 있다면 추가해주는 역할이다. 즉 내부에 @Bean이 있어야 의미가 있다.
@DataJpaTest
@Import(QuerydslConfig.class) // 1
class CustomPostRepositoryImplTest {

    @Autowired
    PostRepository postRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @PersistenceContext
    EntityManager em;


    @Test
    void findAllByConditionTest() { // 2
        // given
        List<Member> members = saveMember(3);
        List<Category> categories = saveCategory(2);

        // 0 - (m0, c0)
        // 1 - (m1, c1)
        // 2 - (m2, c0)
        // 3 - (m0, c1)
        // 4 - (m1, c0)
        // 5 - (m2, c1)
        // 6 - (m0, c0)
        // 7 - (m1, c1)
        // 8 - (m2, c0)
        // 9 - (m0, c1)
        List<Post> posts = IntStream.range(0, 10)
                .mapToObj(i -> postRepository.save(createPost(members.get(i % 3), categories.get(i % 2))))
                .collect(toList());
        clear();

        List<Long> categoryIds = List.of(categories.get(1).getId());
        List<Long> memberIds = List.of(members.get(0).getId(), members.get(2).getId());
        int sizePerPage = 2;
        long expectedTotalElements = 3;

        PostReadCondition page0Cond = createPostReadCondition(0, sizePerPage, categoryIds, memberIds);
        PostReadCondition page1Cond = createPostReadCondition(1, sizePerPage, categoryIds, memberIds);

        // when
        Page<PostSimpleDto> page0 = postRepository.findAllByCondition(page0Cond);
        Page<PostSimpleDto> page1 = postRepository.findAllByCondition(page1Cond);

        // then
        assertThat(page0.getTotalElements()).isEqualTo(expectedTotalElements);
        assertThat(page0.getTotalPages()).isEqualTo((expectedTotalElements + 1) / sizePerPage);

        assertThat(page0.getContent().size()).isEqualTo(2);
        assertThat(page1.getContent().size()).isEqualTo(1);

        // 9 - (m0, c1)
        // 5 - (m2, c1)
        assertThat(page0.getContent().get(0).getId()).isEqualTo(posts.get(9).getId());
        assertThat(page0.getContent().get(1).getId()).isEqualTo(posts.get(5).getId());
        assertThat(page0.hasNext()).isTrue();

        // 3 - (m0, c1)
        assertThat(page1.getContent().get(0).getId()).isEqualTo(posts.get(3).getId());
        assertThat(page1.hasNext()).isFalse();
    }

    private List<Member> saveMember(int size) {
        return IntStream.range(0, size)
                .mapToObj(i -> memberRepository.save(createMember("member" + i, "member" + i, "member" + i, "member" + i)))
                .collect(toList());
    }

    private List<Category> saveCategory(int size) {
        return IntStream.range(0, size)
                .mapToObj(i -> categoryRepository.save(createCategoryWithName("category" + i))).collect(toList());
    }

    private void clear() {
        em.flush();
        em.clear();
    }
}