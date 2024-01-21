package org.example.basicMarket.exception;

public class MemberEmailAlreadyExistsException extends RuntimeException{

    public MemberEmailAlreadyExistsException(String message){
        super(message);
    }
}
