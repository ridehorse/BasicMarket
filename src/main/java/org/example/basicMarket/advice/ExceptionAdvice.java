package org.example.basicMarket.advice;
import org.example.basicMarket.exception.AuthenticationEntryPointException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.basicMarket.dto.response.Response;
import org.example.basicMarket.exception.*;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;



@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ExceptionAdvice {

    private final MessageSource messageSource;

    // @ExceptionHandler에 예외 클래스를 지정해주면, 실행 중에 지정한 예외가 발생하면 해당 메소드를 실행해줍니다.
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // @ResponseStatus로 각 예외마다 상태 코드를 지정해줄 수 있습니다.
    public Response exception(Exception e){

        log.info("e={}",e.getMessage());
        return getFailureResponse("exception.code","exception.msg");
    }

    @ExceptionHandler(AuthenticationEntryPointException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED) // 401
    public Response authenticationEntryPoint() {

        return getFailureResponse("authenticationEntryPoint.code", "authenticationEntryPoint.msg");
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN) // 403
    public Response accessDeniedException() {
        return getFailureResponse("accessDeniedException.code", "accessDeniedException.msg");
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response methodArgumentNotValidException(BindException e) { // 2
        return getFailureResponse("bindException.code", "bindException.msg", e.getBindingResult().getFieldError().getDefaultMessage());
    }

    @ExceptionHandler(LoginFailureException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Response loginFailureException() { // 3
        return getFailureResponse("loginFailureException.code", "loginFailureException.msg");
    }

    @ExceptionHandler(MemberEmailAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Response memberEmailAlreadyExistsException(MemberEmailAlreadyExistsException e) { // 4
        return getFailureResponse("memberEmailAlreadyExistsException.code", "memberEmailAlreadyExistsException.msg",e.getMessage());
    }

    @ExceptionHandler(MemberNickNameAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Response memberNicknameAlreadyExistsException(MemberNickNameAlreadyExistsException e) { // 5
        return getFailureResponse("memberNicknameAlreadyExistsException.code", "memberNicknameAlreadyExistsException.msg",e.getMessage());
    }

    @ExceptionHandler(MemberNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response memberNotFoundException() { // 6
        return getFailureResponse("memberNotFoundException.code", "memberNotFoundException.msg");
    }

    @ExceptionHandler(RoleNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response roleNotFoundException() { // 7
        return getFailureResponse("roleNotFoundException.code", "roleNotFoundException.msg");
    }




    @ExceptionHandler(MissingRequestHeaderException.class) // @RequestHeader 에너테이션 적용됬곳에 값이 null일 경우
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response missingRequestHeaderException(MissingRequestHeaderException e) {
        return getFailureResponse("missingRequestHeaderException.code", "missingRequestHeaderException.msg",e.getHeaderName());
    }

    @ExceptionHandler(CategoryNotFoundException.class) // @RequestHeader 에너테이션 적용됬곳에 값이 null일 경우
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response categoryNotFoundException() {

        return getFailureResponse("categoryNotFoundException.code", "categoryNotFoundException.msg");
    }

    @ExceptionHandler(CannotConvertNestedStructureException.class) // @RequestHeader 에너테이션 적용됬곳에 값이 null일 경우
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Response cannotConvertNestedStructureException(CannotConvertNestedStructureException e) {
        log.info("e={}",e.getMessage());
        return getFailureResponse("cannotConvertNestedStructureException.code", "cannotConvertNestedStructureException.msg");
    }

    @ExceptionHandler(UnsupportedImageFormatException.class) // @RequestHeader 에너테이션 적용됬곳에 값이 null일 경우
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response unsupportedImageFormatException() {
        return getFailureResponse("unsupportedImageFormatException.code", "unsupportedImageFormatException.msg");
    }

    @ExceptionHandler(PostNotFoundException.class) // @RequestHeader 에너테이션 적용됬곳에 값이 null일 경우
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response postNotFoundException() {

        return getFailureResponse("postNotFoundException.code", "postNotFoundException.msg");
    }

    @ExceptionHandler(FileUploadFailureException.class) // @RequestHeader 에너테이션 적용됬곳에 값이 null일 경우
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response fileUploadFailureException(FileUploadFailureException e) {
        log.info("e={}",e.getMessage());
        return getFailureResponse("fileUploadFailureException.code", "fileUploadFailureException.msg");
    }

    @ExceptionHandler(CommentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response commentNotFoundException() {

        return getFailureResponse("commentNotFoundException.code", "commentNotFoundException.msg");
    }

    @ExceptionHandler(MessageNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response messageNotFoundException() {

        return getFailureResponse("messageNotFoundException.code", "messageNotFoundException.msg");
    }

    private Response getFailureResponse(String codeKey, String messageKey) {
        log.info("code = {}, msg = {}", getCode(codeKey), getMessage(messageKey, null));
        return Response.failure(getCode(codeKey), getMessage(messageKey, null));
    }

    private Response getFailureResponse(String codeKey, String messageKey, Object... args) {
        return Response.failure(getCode(codeKey), getMessage(messageKey, args));
    }

    private Integer getCode(String key) {
        return Integer.valueOf(messageSource.getMessage(key, null, null));
    }

    private String getMessage(String key, Object... args) {
        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }
}
