package org.example.basicMarket.dto.message;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import static java.util.stream.Collectors.toSet;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.basicMarket.factory.entity.MessageCreateRequestFactory.createMessageCreateRequest;

public class MessageCreateRequestValidationTest {

    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void validateTest() {
        // given
        MessageCreateRequest req = createMessageCreateRequest("content", null, 2L);

        // when
        Set<ConstraintViolation<MessageCreateRequest>> validate = validator.validate(req);

        // then
        assertThat(validate).isEmpty();
    }

    @Test
    void invalidateByEmptyContentTest() {
        // given
        String invalidValue = null;
        MessageCreateRequest req = createMessageCreateRequest(invalidValue, null, 2L);

        // when
        Set<ConstraintViolation<MessageCreateRequest>> validate = validator.validate(req);

        // then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map(v -> v.getInvalidValue()).collect(toSet())).contains(invalidValue);
    }

    @Test
    void invalidateByBlankContentTest() {
        // given
        String invalidValue = "  ";
        MessageCreateRequest req = createMessageCreateRequest(invalidValue, null, 2L);

        // when
        Set<ConstraintViolation<MessageCreateRequest>> validate = validator.validate(req);

        // then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map(v -> v.getInvalidValue()).collect(toSet())).contains(invalidValue);
    }

    @Test
    void invalidateByNotNullMemberIdTest() {
        // given
        Long invalidValue = 1L;
        MessageCreateRequest req = createMessageCreateRequest("content", invalidValue, 2L);

        // when
        Set<ConstraintViolation<MessageCreateRequest>> validate = validator.validate(req);

        // then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map(v -> v.getInvalidValue()).collect(toSet())).contains(invalidValue);
    }

    @Test
    void invalidateByNullReceiverIdTest() {
        // given
        Long invalidValue = null;
        MessageCreateRequest req = createMessageCreateRequest("content", null, invalidValue);

        // when
        Set<ConstraintViolation<MessageCreateRequest>> validate = validator.validate(req);

        // then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map(v -> v.getInvalidValue()).collect(toSet())).contains(invalidValue);
    }

    @Test
    void invalidateByNegativeOrZeroReceiverIdTest() {
        // given
        Long invalidValue = 0L;
        MessageCreateRequest req = createMessageCreateRequest("content", 1L, invalidValue);

        // when
        Set<ConstraintViolation<MessageCreateRequest>> validate = validator.validate(req);

        // then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map(v -> v.getInvalidValue()).collect(toSet())).contains(invalidValue);
    }
}
