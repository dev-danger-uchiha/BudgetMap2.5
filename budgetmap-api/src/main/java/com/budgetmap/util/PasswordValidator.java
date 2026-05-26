package com.budgetmap.util;

import com.budgetmap.exception.PasswordInvalidoException;
import lombok.extern.slf4j.Slf4j;

/**
 * Validador de contraseñas basado en OWASP Password Guidelines
 * https://cheatsheetseries.owasp.org/cheatsheets/Authentication_Cheat_Sheet.html#password-requirements
 */
@Slf4j
public class PasswordValidator {

    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 128;
    private static final String UPPERCASE_PATTERN = ".*[A-Z].*";
    private static final String LOWERCASE_PATTERN = ".*[a-z].*";
    private static final String DIGIT_PATTERN = ".*[0-9].*";
    private static final String SPECIAL_CHAR_PATTERN = ".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>?/`~].*";

    /**
     * Valida una contraseña según criterios OWASP
     *
     * @param password contraseña a validar
     * @throws PasswordInvalidoException si no cumple con los requisitos
     */
    public static void validar(String password) {
        if (password == null || password.isEmpty()) {
            throw new PasswordInvalidoException("La contraseña es requerida");
        }

        if (password.length() < MIN_LENGTH) {
            throw new PasswordInvalidoException(
                    String.format("La contraseña debe tener al menos %d caracteres (actual: %d)",
                    MIN_LENGTH, password.length()));
        }

        if (password.length() > MAX_LENGTH) {
            throw new PasswordInvalidoException(
                    String.format("La contraseña no debe exceder %d caracteres", MAX_LENGTH));
        }

        if (!password.matches(UPPERCASE_PATTERN)) {
            throw new PasswordInvalidoException(
                    "La contraseña debe contener al menos una mayúscula (A-Z)");
        }

        if (!password.matches(LOWERCASE_PATTERN)) {
            throw new PasswordInvalidoException(
                    "La contraseña debe contener al menos una minúscula (a-z)");
        }

        if (!password.matches(DIGIT_PATTERN)) {
            throw new PasswordInvalidoException(
                    "La contraseña debe contener al menos un número (0-9)");
        }

        if (!password.matches(SPECIAL_CHAR_PATTERN)) {
            throw new PasswordInvalidoException(
                    "La contraseña debe contener al menos un símbolo especial (!@#$%^&* etc)");
        }

        log.debug("Validación de contraseña exitosa - Cumple con criterios OWASP");
    }

    /**
     * Verifica si una contraseña es fuerte (cumple con OWASP) sin lanzar excepciones
     *
     * @param password contraseña a validar
     * @return true si es válida, false en caso contrario
     */
    public static boolean esValida(String password) {
        try {
            validar(password);
            return true;
        } catch (PasswordInvalidoException e) {
            log.debug("Contraseña no válida: {}", e.getMessage());
            return false;
        }
    }
}
