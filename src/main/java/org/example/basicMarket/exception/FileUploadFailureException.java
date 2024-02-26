package org.example.basicMarket.exception;

public class FileUploadFailureException extends RuntimeException{

    public FileUploadFailureException(Throwable cause){
        super(cause);
    }
}
