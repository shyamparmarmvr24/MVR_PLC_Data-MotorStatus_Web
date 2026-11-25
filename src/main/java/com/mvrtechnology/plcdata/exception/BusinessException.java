package com.mvrtechnology.plcdata.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {

    private final HttpStatus status;

    private final String field;

    public BusinessException(
            HttpStatus status,
            String field,
            String message
    ) {
        super(message);
        this.status = status;
        this.field = field;
    }
}