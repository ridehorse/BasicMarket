package org.example.basicMarket.advice;

import lombok.extern.slf4j.Slf4j;
import org.example.basicMarket.dto.response.Response;
import org.example.basicMarket.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;



@RestControllerAdvice
@Slf4j
public class ExceptionAdvice {

    // @ExceptionHandler에 예외 클래스를 지정해주면, 실행 중에 지정한 예외가 발생하면 해당 메소드를 실행해줍니다.
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // @ResponseStatus로 각 예외마다 상태 코드를 지정해줄 수 있습니다.
    public Response exception(Exception e){

        log.info("e={}",e.getMessage());
        return Response.failure(-1000,"오류가 발생하였습니다.");
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response methodArgumentNotValidException(BindException e) { // 2
        return Response.failure(-1003, e.getBindingResult().getFieldError().getDefaultMessage());
    }

    @ExceptionHandler(LoginFailureException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Response loginFailureException() { // 3
        return Response.failure(-1004, "로그인에 실패하였습니다.");
    }

    @ExceptionHandler(MemberEmailAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Response memberEmailAlreadyExistsException(MemberEmailAlreadyExistsException e) { // 4
        return Response.failure(-1005, e.getMessage() + "은 중복된 이메일 입니다.");
    }

    @ExceptionHandler(MemberNickNameAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Response memberNicknameAlreadyExistsException(MemberNickNameAlreadyExistsException e) { // 5
        return Response.failure(-1006, e.getMessage() + "은 중복된 닉네임 입니다.");
    }

    @ExceptionHandler(MemberNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response memberNotFoundException() { // 6
        return Response.failure(-1007, "요청한 회원을 찾을 수 없습니다.");
    }

    @ExceptionHandler(RoleNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response roleNotFoundException() { // 7
        return Response.failure(-1008, "요청한 권한 등급을 찾을 수 없습니다.");
    }

    @ExceptionHandler(AuthenticationEntryPointException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED) // 401
    public Response authenticationEntryPoint() {
        return Response.failure(-1001, "인증되지 않은 사용자입니다.");
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN) // 403
    public Response accessDeniedException() {
        return Response.failure(-1002, "접근이 거부되었습니다.");
    }

    @ExceptionHandler(MissingRequestHeaderException.class) // @RequestHeader 에너테이션 적용됬곳에 값이 null일 경우
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response missingRequestHeaderException(MissingRequestHeaderException e) {
        return Response.failure(-1009, e.getHeaderName() + " 요청 헤더가 누락되었습니다.");
    }

    @ExceptionHandler(CategoryNotFoundException.class) // @RequestHeader 에너테이션 적용됬곳에 값이 null일 경우
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response categoryNotFoundException() {
        return Response.failure(-1010," 요청한 카테고리를 찾을 수 없습니다.");
    }

    @ExceptionHandler(CannotConvertNestedStructureException.class) // @RequestHeader 에너테이션 적용됬곳에 값이 null일 경우
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Response CannotConvertNestedStructureException(CannotConvertNestedStructureException e) {
        log.info("e={}",e.getMessage());
        return Response.failure(-1011,"중첩 구조 변환에 실패하였습니다");
    }

    @ExceptionHandler(UnsupportedImageFormatException.class) // @RequestHeader 에너테이션 적용됬곳에 값이 null일 경우
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response UnsupportedImageFormatException() {
        return Response.failure(-1012,"지원되지 않는 확장자 입니다.");
    }

    @ExceptionHandler(PostNotFoundException.class) // @RequestHeader 에너테이션 적용됬곳에 값이 null일 경우
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response PostNotFoundtException() {
        return Response.failure(-1013,"게시물을 찾을 수 없습니다.");
    }

    @ExceptionHandler(FIleUploadFailureException.class) // @RequestHeader 에너테이션 적용됬곳에 값이 null일 경우
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response FIleUploadFailureException(FIleUploadFailureException e) {
        log.info("e={}",e.getMessage());
        return Response.failure(-1014,"파일 업로드에 실패하였습니다.");
    }

    @ExceptionHandler(CommentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response commentNotFoundException() {
        return Response.failure(-1015,"댓글을 찾을 수 없습니다.");
    }
}
