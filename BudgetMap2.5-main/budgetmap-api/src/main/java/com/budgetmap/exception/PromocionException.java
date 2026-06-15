package com.budgetmap.exception;

public class PromocionException extends RuntimeException {
    public PromocionException(String message) {
        super(message);
    }

    public PromocionException(String message, Throwable cause) {
        super(message, cause);
    }
}
