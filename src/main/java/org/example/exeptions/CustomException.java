package org.example.exeptions;

public class CustomException extends IllegalArgumentException{
    public CustomException(String messege){
        super(messege);
    }
}
