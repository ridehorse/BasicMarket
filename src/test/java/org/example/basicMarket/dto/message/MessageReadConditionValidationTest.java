package org.example.basicMarket.dto.message;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.example.basicMarket.factory.entity.MessageReadConditionFactory.createMessageReadCondition;

class MessageReadConditionValidationTest {
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void validateTest() {
        // given
        MessageReadCondition cond = createMessageReadCondition(null, 1L, 1);

        // when
        Set<ConstraintViolation<MessageReadCondition>> validate = validator.validate(cond);

        // then
        assertThat(validate).isEmpty();
    }

    @Test
    void invalidateByNotNullMemberIdTest() {
        // given
        Long invalidValue = 1L;
        MessageReadCondition cond = createMessageReadCondition(invalidValue, 1L, 1);

        // when
        Set<ConstraintViolation<MessageReadCondition>> validate = validator.validate(cond);

        // then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map(v -> v.getInvalidValue()).collect(toSet())).contains(invalidValue);
    }

    @Test
    void invalidateByNullSizeTest() {
        // given
        Integer invalidValue = null;
        MessageReadCondition cond = createMessageReadCondition(null, 1L, invalidValue);

        // when
        Set<ConstraintViolation<MessageReadCondition>> validate = validator.validate(cond);

        // then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map(v -> v.getInvalidValue()).collect(toSet())).contains(invalidValue);
    }

    @Test
    void invalidateByNegativeOrZeroSizeTest() {
        // given
        Integer invalidValue = 0;
        MessageReadCondition cond = createMessageReadCondition(null, 1L, invalidValue);

        // when
        Set<ConstraintViolation<MessageReadCondition>> validate = validator.validate(cond);

        // then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map(v -> v.getInvalidValue()).collect(toSet())).contains(invalidValue);
    }
}
