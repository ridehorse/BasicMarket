package org.example.basicMarket.config.token;

import org.apache.el.parser.Token;
import org.example.basicMarket.handler.JwtHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TokenHelperTest {

    // 직접 생성자로 초기화한 경우에는 실제 JwtHandler의 인스턴스를 생성하여 주입받습니다.
    // 이 경우 실제 동작을 가진 객체를 사용하게 됩니다.
    TokenHelper tokenHelper;

    //따라서 @Mock 어노테이션을 사용한 경우에는 JwtHandler의 실제 동작이 아니라 Mockito가 생성한 목 객체를 주입받습니다.
    // 이 목 객체는 실제 동작을 가지지 않으며, 테스트 중에 지정한 동작을 수행합니다.
    @Mock
    JwtHandler jwtHandler;

    @BeforeEach
    void beforeEach(){
        tokenHelper = new TokenHelper(jwtHandler, "key", 1000L);
    }

    @Test
    void createTokenTest() {
        // given
        given(jwtHandler.createToken(anyString(), anyString(), anyLong())).willReturn("token");

        // when
        String createdToken = tokenHelper.createToken("subject");

        // then
        assertThat(createdToken).isEqualTo("token");
        verify(jwtHandler).createToken(anyString(), anyString(), anyLong());
    }

    @Test
    void validateTest() {
        // given
        given(jwtHandler.validate(anyString(), anyString())).willReturn(true);

        // when
        boolean result = tokenHelper.validate("token");

        // then
        assertThat(result).isTrue();
    }

    @Test
    void invalidateTest() {
        // given
        given(jwtHandler.validate(anyString(), anyString())).willReturn(false);

        // when
        boolean result = tokenHelper.validate("token");

        // then
        assertThat(result).isFalse();
    }

    @Test
    void extractSubjectTest() {
        // given
        given(jwtHandler.extractSubject(anyString(), anyString())).willReturn("subject");

        // when
        String subject = tokenHelper.extractSubject("token");

        // then
        assertThat(subject).isEqualTo(subject);
    }

}
