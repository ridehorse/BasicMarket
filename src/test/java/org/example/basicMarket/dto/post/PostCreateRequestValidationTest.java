package org.example.basicMarket.dto.post;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.example.basicMarket.factory.dto.PostCreateRequestFactory.*;

public class PostCreateRequestValidationTest {
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void validateTest() {
        // given
        PostCreateRequest req = createPostCreateRequestWithMemberId(null);

        // when
        Set<ConstraintViolation<PostCreateRequest>> validate = validator.validate(req);

        // then
        assertThat(validate).isEmpty();
    }

    @Test
    void invalidateByEmptyTitleTest() {
        // given
        String invalidValue = null;
        PostCreateRequest req = createPostCreateRequestWithTitle(invalidValue);

        // when
        Set<ConstraintViolation<PostCreateRequest>> validate = validator.validate(req);

        // then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map(v -> v.getInvalidValue()).collect(toSet())).contains(invalidValue);
    }

    @Test
    void invalidateByBlankTitleTest() {
        // given
        String invalidValue = " ";
        PostCreateRequest req = createPostCreateRequestWithTitle(invalidValue);

        // when
        Set<ConstraintViolation<PostCreateRequest>> validate = validator.validate(req);

        // then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map(v -> v.getInvalidValue()).collect(toSet())).contains(invalidValue);
    }

    @Test
    void invalidateByEmptyContentTest() {
        // given
        String invalidValue = null;
        PostCreateRequest req = createPostCreateRequestWithContent(invalidValue);

        // when
        Set<ConstraintViolation<PostCreateRequest>> validate = validator.validate(req);

        // then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map(v -> v.getInvalidValue()).collect(toSet())).contains(invalidValue);
    }

    @Test
    void invalidateByBlankContentTest() {
        // given
        String invalidValue = " ";
        PostCreateRequest req = createPostCreateRequestWithContent(invalidValue);

        // when
        Set<ConstraintViolation<PostCreateRequest>> validate = validator.validate(req);

        // then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map(v -> v.getInvalidValue()).collect(toSet())).contains(invalidValue);
    }

    @Test
    void invalidateByNullPriceTest() {
        // given
        Long invalidValue = null;
        PostCreateRequest req = createPostCreateRequestWithPrice(invalidValue);

        // when
        Set<ConstraintViolation<PostCreateRequest>> validate = validator.validate(req);

        // then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map(v -> v.getInvalidValue()).collect(toSet())).contains(invalidValue);
    }

    @Test
    void invalidateByNegativePriceTest() {
        // given
        Long invalidValue = -1L;
        PostCreateRequest req = createPostCreateRequestWithPrice(invalidValue);

        // when
        Set<ConstraintViolation<PostCreateRequest>> validate = validator.validate(req);

        // then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map(v -> v.getInvalidValue()).collect(toSet())).contains(invalidValue);
    }

    @Test
    void invalidateByNotNullMemberIdTest() {
        // given
        Long invalidValue = 1L;
        PostCreateRequest req = createPostCreateRequestWithMemberId(invalidValue);

        // when
        Set<ConstraintViolation<PostCreateRequest>> validate = validator.validate(req);

        // then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map(v -> v.getInvalidValue()).collect(toSet())).contains(invalidValue);
    }

    @Test
    void invalidateByNullCategoryIdTest() {
        // given
        Long invalidValue = null;
        PostCreateRequest req = createPostCreateRequestWithCategoryId(invalidValue);

        // when
        Set<ConstraintViolation<PostCreateRequest>> validate = validator.validate(req);

        // then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map(v -> v.getInvalidValue()).collect(toSet())).contains(invalidValue);
    }

    @Test
    void invalidateByNegativeCategoryIdTest() {
        // given
        Long invalidValue = -1L;
        PostCreateRequest req = createPostCreateRequestWithCategoryId(invalidValue);

        // when
        Set<ConstraintViolation<PostCreateRequest>> validate = validator.validate(req);

        // then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map(v -> v.getInvalidValue()).collect(toSet())).contains(invalidValue);
    }
}
