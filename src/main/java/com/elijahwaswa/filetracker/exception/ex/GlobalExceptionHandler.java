package com.elijahwaswa.filetracker.exception.ex;

import com.elijahwaswa.filetracker.exception.ErrorPayload;
import com.elijahwaswa.filetracker.exception.IErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorPayload> handleGlobalException(Exception exception, WebRequest webRequest) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorPayload errorPayload = new ErrorPayload();
        errorPayload.setTimestamp(LocalDateTime.now());
        errorPayload.setMessage(exception.getMessage());

        if (exception instanceof IErrorDetails iErrorDetails) {
            errorPayload.setTimestamp(iErrorDetails.getTimeStamp());
            httpStatus = iErrorDetails.getHttpStatus();
        } else if (exception instanceof ErrorResponse errorResponse) {
            httpStatus = HttpStatus.valueOf(errorResponse.getStatusCode().value());
        }

        return new ResponseEntity<>(errorPayload, httpStatus);
    }
}
