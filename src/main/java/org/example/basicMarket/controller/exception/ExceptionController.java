package org.example.basicMarket.controller.exception;

import org.example.basicMarket.exception.AccessDeniedException;
import org.example.basicMarket.exception.AuthenticationEntryPointException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore // 문서로 만들지 않음
@RestController
public class ExceptionController {

    // 해당 리소스에 접근하려면 사용자를 인증해야 하는데, 사용자가 인증되지 않은 상태에서 요청이 발생한 경우
    @GetMapping("/exception/entry-point")
    public void entryPoint() {
        throw new AuthenticationEntryPointException();
    }

    // 사용자가 특정 리소스에 대한 필요한 권한이 없을 때 발생합니다. 인증은 되어 있지만 해당 리소스에 접근할 수 있는 권한이 부족한 경우
    @GetMapping("/exception/access-denied")
    public void accessDenied() {
        throw new AccessDeniedException();
    }
}
