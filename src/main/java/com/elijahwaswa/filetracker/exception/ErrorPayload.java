package com.elijahwaswa.filetracker.exception;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorPayload {
    private LocalDateTime timestamp;
    private String message;
}
