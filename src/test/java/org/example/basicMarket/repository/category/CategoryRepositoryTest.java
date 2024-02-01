package org.example.basicMarket.repository.category;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.example.basicMarket.config.QuerydslConfig;
import org.example.basicMarket.entity.category.Category;
import org.example.basicMarket.exception.CategoryNotFoundException;
import org.example.basicMarket.repository.member.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.example.basicMarket.factory.entity.CategoryFactory.createCategory;
import static org.example.basicMarket.factory.entity.CategoryFactory.createCategoryWithName;

// JPA 관련 테스트를 지원합니다.
// 이 어노테이션을 사용하면 JPA 엔터티와 관련된 부분을 중점적으로 테스트할 수 있습니다.
//임베디드 데이터베이스 설정: @DataJpaTest를 사용하면 내장형 데이터베이스 (예: H2)를 사용하여 테스트를 실행할 수 있습니다. 이는 테스트 실행 시 별도의 데이터베이스를 구성할 필요 없이 테스트를 수행할 수 있도록 도와줍니다.
//
//JPA 관련 빈들의 자동 구성: @DataJpaTest는 JPA 관련된 빈들만을 스캔하여 테스트에 필요한 빈들을 자동으로 구성합니다. 이는 테스트의 성능을 향상시키고, 불필요한 빈들을 포함하지 않도록 도와줍니다.
//
//트랜잭션 관리: 테스트 메서드 실행 전에 트랜잭션을 시작하고, 테스트가 완료되면 롤백하는 방식으로 트랜잭션을 관리합니다. 이를 통해 각각의 테스트가 서로 영향을 주지 않고 독립적으로 실행될 수 있습니다.
@DataJpaTest
@Import(QuerydslConfig.class)
public class CategoryRepositoryTest {

    @Autowired
    CategoryRepository categoryRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    void createAndReadTest() {
        // given
        Category category = createCategory();

        // when
        Category savedCategory = categoryRepository.save(category);
        clear();

        // then
        Category foundCategory = categoryRepository.findById(savedCategory.getId()).orElseThrow(CategoryNotFoundException::new);
        assertThat(foundCategory.getId()).isEqualTo(savedCategory.getId());
    }

    @Test
    void readAllTest() {
        // given
        List<Category> categories = List.of("name1", "name2", "name3").stream().map(n -> createCategoryWithName(n)).collect(toList());
        categoryRepository.saveAll(categories);
        clear();

        // when
        List<Category> foundCategories = categoryRepository.findAll();

        // then
        assertThat(foundCategories.size()).isEqualTo(3);
    }

    @Test
    void deleteTest() {
        // given
        Category category = categoryRepository.save(createCategory());
        clear();

        // when
        categoryRepository.delete(category);
        clear();

        // then
        assertThatThrownBy(() -> categoryRepository.findById(category.getId()).orElseThrow(CategoryNotFoundException::new))
                .isInstanceOf(CategoryNotFoundException.class);
    }

    @Test
    void deleteCascadeTest() {
        // given
        Category category1 = categoryRepository.save(createCategoryWithName("category1"));
        Category category2 = categoryRepository.save(createCategory("category2", category1));
        Category category3 = categoryRepository.save(createCategory("category3", category2));
        Category category4 = categoryRepository.save(createCategoryWithName("category4"));
        clear();

        // when
        categoryRepository.deleteById(category1.getId());
        clear();

        // then
        List<Category> result = categoryRepository.findAll();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getId()).isEqualTo(category4.getId());
    }

    @Test
    void deleteNoneValueTest() {
        // given
        Long noneValueId = 100L;

        // when, then
        assertThatThrownBy(() -> categoryRepository.deleteById(noneValueId))
                .isInstanceOf(EmptyResultDataAccessException.class);
    }

    @Test
    void findAllWithParentOrderByParentIdAscNullsFirstCategoryIdAscTest() {
        // given
        // 1		NULL
        // 2		1
        // 3		1
        // 4		2
        // 5		2
        // 6		4
        // 7		3
        // 8		NULL
        Category c1 = categoryRepository.save(createCategory("category1", null));
        Category c2 = categoryRepository.save(createCategory("category2", c1));
        Category c3 = categoryRepository.save(createCategory("category3", c1));
        Category c4 = categoryRepository.save(createCategory("category4", c2));
        Category c5 = categoryRepository.save(createCategory("category5", c2));
        Category c6 = categoryRepository.save(createCategory("category6", c4));
        Category c7 = categoryRepository.save(createCategory("category7", c3));
        Category c8 = categoryRepository.save(createCategory("category8", null));
        clear();

        // when
        List<Category> result = categoryRepository.findAllOrderByParentIdAscNullsFirstCategoryIdAsc();

        // then
        // 1	NULL
        // 8	NULL
        // 2	1
        // 3	1
        // 4	2
        // 5	2
        // 7	3
        // 6	4
        assertThat(result.size()).isEqualTo(8);
        assertThat(result.get(0).getId()).isEqualTo(c1.getId());
        assertThat(result.get(1).getId()).isEqualTo(c8.getId());
        assertThat(result.get(2).getId()).isEqualTo(c2.getId());
        assertThat(result.get(3).getId()).isEqualTo(c3.getId());
        assertThat(result.get(4).getId()).isEqualTo(c4.getId());
        assertThat(result.get(5).getId()).isEqualTo(c5.getId());
        assertThat(result.get(6).getId()).isEqualTo(c7.getId());
        assertThat(result.get(7).getId()).isEqualTo(c6.getId());

    }

    void clear() {
        em.flush();
        em.clear();
    }
}
