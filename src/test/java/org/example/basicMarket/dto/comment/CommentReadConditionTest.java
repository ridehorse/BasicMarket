package org.example.basicMarket.dto.comment;

import jakarta.validation.ConstraintViolation;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.Set;


import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.example.basicMarket.factory.dto.CommentReadConditionFactory.createCommentReadCondition;

public class CommentReadConditionTest {

    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void validateTest() {
        // given
        CommentReadCondition cond = createCommentReadCondition();

        // when
        Set<ConstraintViolation<CommentReadCondition>> validate = validator.validate(cond);

        // then
        assertThat(validate).isEmpty();
    }

    @Test
    void invalidateByNegativePostIdTest() {
        // given
        Long invalidValue = -1L;
        CommentReadCondition cond = createCommentReadCondition(invalidValue);

        // when
        Set<ConstraintViolation<CommentReadCondition>> validate = validator.validate(cond);

        // then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map(v -> v.getInvalidValue()).collect(toSet())).contains(invalidValue);
    }
}
