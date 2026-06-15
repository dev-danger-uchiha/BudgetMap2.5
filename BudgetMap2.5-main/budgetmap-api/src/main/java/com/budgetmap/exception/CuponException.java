package com.budgetmap.exception;

public class CuponException extends RuntimeException {
    public CuponException(String message) {
        super(message);
    }

    public CuponException(String message, Throwable cause) {
        super(message, cause);
    }
}
