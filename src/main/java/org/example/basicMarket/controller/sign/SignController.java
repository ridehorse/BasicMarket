package org.example.basicMarket.controller.sign;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.basicMarket.dto.response.Response;
import org.example.basicMarket.dto.sign.SignInRequest;
import org.example.basicMarket.dto.sign.SignUpRequest;
import org.example.basicMarket.service.sign.SignService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import static org.example.basicMarket.dto.response.Response.success;

@Api(value = "Sign Controller",tags = "Sign") // Swagger로 API 문서가 작성될 것
@RestController // 객체를 반환하면 JSON으로 변환해준다.
@RequiredArgsConstructor
public class SignController {

    private final SignService signService;

    @ApiOperation(value = "회원가입",notes = "회원가입을 한다")
    @PostMapping("/api/sign-up")
    @ResponseStatus(HttpStatus.CREATED) // 요청에 대한 반환(응답) 코드를 201로 한다.
    public Response signUp(@Valid @RequestBody SignUpRequest req){ // @valid : req 객체의 필드 값을 검증함, @RequestBody : 요청으로 전달받은 JSON 바디를 객체로 변환하기 위함
        signService.signUp(req);
        return success();
    }
    @ApiOperation(value = "로그인", notes = "로그인을 한다.")
    @PostMapping("/api/sign-in")
    @ResponseStatus(HttpStatus.OK) // 요청에 대한 반환(응답) 코드를 200으로 한다.
    public Response signIn(@Valid @RequestBody SignInRequest req) { // 3
        return success(signService.signIn(req));
    }

    // 요청에 포함되는 Authorization 헤더는 이미 전역적으로 지정되도록 설정해두었기 때문에,
    // 해당 API에 필요한 요청 헤더는 @ApiIgnore를 선언해줍니다
    @ApiOperation(value = "토큰 재발급", notes = "리프레시 토큰으로 새로운 액세스 토큰을 발급 받는다.")
    @PostMapping("/api/refresh-token")
    @ResponseStatus(HttpStatus.OK) // 파라미터에 설정된 @RequestHeader는 required 옵션의 기본 설정 값이 true이기 때문에, 이 헤더 값이 전달되지 않았을 때 예외가 발생하게 됩니다
    public Response refreshToken(@ApiIgnore @RequestHeader(value = "Authorization") String refreshToken) {
        return success(signService.refreshToken(refreshToken));
    }
}

