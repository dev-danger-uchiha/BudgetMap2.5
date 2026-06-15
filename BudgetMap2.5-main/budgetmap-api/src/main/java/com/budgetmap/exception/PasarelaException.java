package com.budgetmap.exception;

public class PasarelaException extends RuntimeException {
    public PasarelaException(String message) {
        super(message);
    }

    public PasarelaException(String message, Throwable cause) {
        super(message, cause);
    }
}
