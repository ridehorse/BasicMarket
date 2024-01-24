package org.example.basicMarket.controller.exception;

import org.example.basicMarket.exception.AccessDeniedException;
import org.example.basicMarket.exception.AuthenticationEntryPointException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore // 문서로 만들지 않음
@RestController
public class ExceptionController {

    @GetMapping("/exception/entry-point")
    public void entryPoint() {
        throw new AuthenticationEntryPointException();
    }

    @GetMapping("/exception/access-denied")
    public void accessDenied() {
        throw new AccessDeniedException();
    }
}
