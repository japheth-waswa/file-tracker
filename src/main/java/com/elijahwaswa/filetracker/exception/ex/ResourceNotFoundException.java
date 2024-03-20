package com.elijahwaswa.filetracker.exception.ex;

import com.elijahwaswa.filetracker.exception.IErrorDetails;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class ResourceNotFoundException extends RuntimeException implements IErrorDetails {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    @Override
    public LocalDateTime getTimeStamp() {
        return LocalDateTime.now();
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.FOUND;
    }
}
