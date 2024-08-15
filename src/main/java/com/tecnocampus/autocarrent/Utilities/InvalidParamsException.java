package com.tecnocampus.autocarrent.Utilities;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
public class InvalidParamsException extends Exception {

    public InvalidParamsException(){
        super();
    }

    public InvalidParamsException(String message){
        super(message);
    }
}
