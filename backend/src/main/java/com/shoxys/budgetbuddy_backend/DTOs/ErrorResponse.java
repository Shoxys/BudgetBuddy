package com.shoxys.budgetbuddy_backend.DTOs;

import com.shoxys.budgetbuddy_backend.Config.Constants;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;

/**
 * Response object for conveying error details in API responses.
 */
public class ErrorResponse {
    private int status;

    @Size(max = Constants.MAX_ERROR_MESSAGE_LENGTH, message = "Error message cannot exceed " + Constants.MAX_ERROR_MESSAGE_LENGTH + " characters")
    private String error;

    @Size(max = Constants.MAX_ERROR_MESSAGE_LENGTH, message = "Message cannot exceed " + Constants.MAX_ERROR_MESSAGE_LENGTH + " characters")
    private String message;

    public ErrorResponse(int status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
    }

    public ErrorResponse(String message, HttpStatus httpStatus) {
        this.status = httpStatus.value();
        this.error = httpStatus.getReasonPhrase();
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}