package com.hardi.Reddit.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SpringRedditException extends RuntimeException{

    public SpringRedditException(String exMessage) {
        super(exMessage);
    }
}
