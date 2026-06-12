package com.example.jobandrecruitment.exception;

import lombok.Getter;

@Getter
public class AppException extends RuntimeException {

    private int status = 400;

    public AppException() {
        super();
    }

    public AppException(String message) {
        super(message);
    }

    public AppException(String message, Throwable cause) {
        super(message, cause);
    }

    public AppException(String message, int status) {
        super(message);
        this.status = status;
    }

}
