package com.budgetmap.exception;

public class PuntosException extends RuntimeException {
    public PuntosException(String message) {
        super(message);
    }

    public PuntosException(String message, Throwable cause) {
        super(message, cause);
    }
}
