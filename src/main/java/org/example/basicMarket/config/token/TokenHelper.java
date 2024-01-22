package org.example.basicMarket.config.token;

import lombok.RequiredArgsConstructor;
import org.example.basicMarket.handler.JwtHandler;

// @Component 로 컨테이너에 등록하지 않는다.
// 왜냐하면 TokenHelper 인스턴스가 TokenConfig 객체내부에서 @Bean으로 등록되어 있기 떄문이다.
@RequiredArgsConstructor
public class TokenHelper {

    private final JwtHandler jwtHandler;
    private final String key;
    private final long maxAgeSeconds;

    public String createToken(String subject) {
        return jwtHandler.createToken(key, subject, maxAgeSeconds);
    }

    public boolean validate(String token) {
        return jwtHandler.validate(key, token);
    }

    public String extractSubject(String token) {
        return jwtHandler.extractSubject(key, token);
    }


}
