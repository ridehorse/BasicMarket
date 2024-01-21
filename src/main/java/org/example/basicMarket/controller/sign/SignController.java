package org.example.basicMarket.controller.sign;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.basicMarket.dto.response.Response;
import org.example.basicMarket.dto.sign.SignInRequest;
import org.example.basicMarket.dto.sign.SignUpRequest;
import org.example.basicMarket.service.sign.SignService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.example.basicMarket.dto.response.Response.success;

@RestController // 객체를 반환하면 JSON으로 변환해준다.
@RequiredArgsConstructor
public class SignController {

    private final SignService signService;

    @PostMapping("/api/sign-up")
    @ResponseStatus(HttpStatus.CREATED) // 요청에 대한 반환(응답) 코드를 201로 한다.
    public Response signUp(@Valid @RequestBody SignUpRequest req){ // @valid : req 객체의 필드 값을 검증함, @RequestBody : 요청으로 전달받은 JSON 바디를 객체로 변환하기 위함
        signService.signUp(req);
        return success();
    }

    @PostMapping("/api/sign-in")
    @ResponseStatus(HttpStatus.OK) // 요청에 대한 반환(응답) 코드를 200으로 한다.
    public Response signIn(@Valid @RequestBody SignInRequest req) { // 3
        return success(signService.signIn(req));
    }

}
