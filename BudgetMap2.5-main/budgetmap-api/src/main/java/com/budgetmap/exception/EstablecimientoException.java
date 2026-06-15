package com.budgetmap.exception;

public class EstablecimientoException extends RuntimeException {
    public EstablecimientoException(String message) {
        super(message);
    }

    public EstablecimientoException(String message, Throwable cause) {
        super(message, cause);
    }
}
