package org.example.basicMarket.dto.post;


import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;


import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.example.basicMarket.factory.dto.PostUpdatedRequestFactory.createPostUpdateRequest;

public class PostUpdateRequestValidationTest {

    //이 코드는 Java에서 Bean Validation API를 사용하여 기본 Validator를 생성하는 방법을 나타냅니다.
    // Bean Validation은 객체의 유효성을 검사하기 위한 자바 표준 API입니다.
    // 주로 Java Bean 객체의 필드나 메서드에 대한 제약 조건을 정의하고, 이러한 제약 조건에 따라 객체의 유효성을 검사할 때 사용됩니다.
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void validateTest(){
        //given
        // 클라이언트 요청에서 필드가 채워지는 PostUpdateRequest 객체의 필드를 Test에서 사용하기 위해 Factory class를 만들어 인위적으로 채워준다.
        PostUpdateRequest req = createPostUpdateRequest("title","content",1234L, List.of(),List.of());

        //when
        //validate() 매서드를 통해 PostUpdateRequest에 정의된 유효성 요소들을 검사한다.
        Set<ConstraintViolation<PostUpdateRequest>> validate = validator.validate(req);

        //then
        //위반된것이 validate 객체에 저장되므로 비어있다면 위반된것이 없는것이다.
        assertThat(validate).isEmpty();
    }

    @Test
    void invalidateByEmptyContentTest(){
        //given
        String invalidValue = null;
        PostUpdateRequest req = createPostUpdateRequest("title", invalidValue, 1234L, List.of(), List.of());

        // when
        Set<ConstraintViolation<PostUpdateRequest>> validate = validator.validate(req);

        // then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map(v -> v.getInvalidValue()).collect(toSet())).contains(invalidValue);
    }

    @Test
    void invalidateByBlankContentTest() {
        // given
        String invalidValue = " ";
        PostUpdateRequest req = createPostUpdateRequest("title", invalidValue, 1234L, List.of(), List.of());

        // when
        Set<ConstraintViolation<PostUpdateRequest>> validate = validator.validate(req);

        // then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map(v -> v.getInvalidValue()).collect(toSet())).contains(invalidValue);
    }

    @Test
    void invalidateByNullPriceTest() {
        // given
        Long invalidValue = null;
        PostUpdateRequest req = createPostUpdateRequest("title", "content", invalidValue, List.of(), List.of());

        // when
        Set<ConstraintViolation<PostUpdateRequest>> validate = validator.validate(req);

        // then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map(v -> v.getInvalidValue()).collect(toSet())).contains(invalidValue);
    }

    @Test
    void invalidateByNegativePriceTest() {
        // given
        Long invalidValue = -1L;
        PostUpdateRequest req = createPostUpdateRequest("title", "content", invalidValue, List.of(), List.of());

        // when
        Set<ConstraintViolation<PostUpdateRequest>> validate = validator.validate(req);

        // then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map(v -> v.getInvalidValue()).collect(toSet())).contains(invalidValue);
    }


}
