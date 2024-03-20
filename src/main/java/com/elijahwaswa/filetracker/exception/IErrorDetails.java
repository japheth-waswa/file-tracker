package com.elijahwaswa.filetracker.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public interface IErrorDetails {
    LocalDateTime getTimeStamp();
    HttpStatus getHttpStatus();
}
