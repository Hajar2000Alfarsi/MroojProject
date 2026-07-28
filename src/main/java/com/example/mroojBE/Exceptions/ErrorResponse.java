package com.example.mroojBE.Exceptions;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Date;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private Date timestamp;
    private int statusCode;
    private HttpStatus status;
    private String error;
    private String message;
    private String path;
    private Map<String, String> fieldErrors;

    // Constructor for standard domain errors (404, 409, 500)
    public ErrorResponse(HttpStatus status, int statusCode, String error, String message, String path) {
        this.status = status;
        this.statusCode = statusCode;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    // Constructor for Bean Validation failures (400)
    public ErrorResponse(HttpStatus status, int statusCode, String error, String message, String path, Map<String, String> fieldErrors) {
        this.status = status;
        this.statusCode = statusCode;
        this.error = error;
        this.message = message;
        this.path = path;
        this.fieldErrors = fieldErrors;
    }
}
