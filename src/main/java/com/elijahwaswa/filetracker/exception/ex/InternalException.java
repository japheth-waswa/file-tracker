package com.elijahwaswa.filetracker.exception.ex;

import com.elijahwaswa.filetracker.exception.IErrorDetails;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class InternalException extends RuntimeException implements IErrorDetails {

    public InternalException(String message) {
        super(message);
    }

    @Override
    public LocalDateTime getTimeStamp() {
        return LocalDateTime.now();
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
