package com.budgetmap.exception;

public class CuentaBloqueadaException extends RuntimeException {
    
    private final long minutosRestantes;

    public CuentaBloqueadaException(String message, long minutosRestantes) {
        super(message);
        this.minutosRestantes = minutosRestantes;
    }

    public long getMinutosRestantes() {
        return minutosRestantes;
    }
}
