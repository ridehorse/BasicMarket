package org.example.basicMarket.exception;

public class MemberNickNameAlreadyExistsException extends RuntimeException{

    public MemberNickNameAlreadyExistsException(String message){
        super(message);
    }
}
