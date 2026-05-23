package com.budgetmap.exception;

public class OperacionNoPermitidaException extends RuntimeException {
    public OperacionNoPermitidaException(String message) {
        super(message);
    }
}