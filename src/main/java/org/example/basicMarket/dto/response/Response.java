package org.example.basicMarket.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL) // null 값을 가지는 필드는, JSON 응답에 포함되지 않도록 한다.(예를 들어 result 필드가 null 이면 result 필드는 응답 요소에 아예 포함되지 않는다.)
@AllArgsConstructor(access = AccessLevel.PRIVATE) // static 팩토리 메소드를 이용하여 인스턴스를 생성하므로, 기본생성자의 접근은 private로 설정한다.
@Getter // 응답 객체를 JSON으로 변환하려면 getter가 필요하다.
public class Response {

    private boolean success; // 요청 성공 여부
    private int code; // 응답 코드(요청 성공 : 0)
    private Result result; // 응답 데이터
    
    // 요청은 성공 했지만 응답해야할 별다른 데이터가 없을 떄
    public static Response success(){

        return new Response(true,0,null);
    }
    
    // 성공했을 때 응답 데이터도 반환
    public static<T> Response success(T data){

        return new Response(true,0,new Success<>(data));
    }
    
    // 실패시 메세지도 반환
    public static Response failure(int code,String msg){

        return new Response(false,code,new Failure(msg));
    }
}
