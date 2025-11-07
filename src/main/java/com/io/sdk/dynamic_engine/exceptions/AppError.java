package com.io.sdk.dynamic_engine.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppError extends RuntimeException {
    private int status;
    private String message;

    public AppError(int status, String message) {
        super(message);
        this.status = status;
        this.message = message;
    }
}
