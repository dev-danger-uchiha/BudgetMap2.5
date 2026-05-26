package com.budgetmap.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

        private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GlobalExceptionHandler.class);

        public record ErrorResponse(
                        LocalDateTime timestamp,
                        int status,
                        String error,
                        String message,
                        String path) {
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public ResponseEntity<Map<String, Object>> handleValidationExceptions(
                        MethodArgumentNotValidException ex, WebRequest request) {

                Map<String, String> errors = new HashMap<>();
                ex.getBindingResult().getAllErrors().forEach(error -> {
                        String fieldName = ((FieldError) error).getField();
                        String errorMessage = error.getDefaultMessage();
                        errors.put(fieldName, errorMessage);
                });

                log.warn("Error de validacion: {}", errors);

                Map<String, Object> response = new HashMap<>();
                response.put("timestamp", LocalDateTime.now());
                response.put("status", HttpStatus.BAD_REQUEST.value());
                response.put("error", "Error de validacion");
                response.put("message", "Los datos enviados no son validos");
                response.put("errors", errors);
                response.put("path", request.getDescription(false).replace("uri=", ""));

                return ResponseEntity.badRequest().body(response);
        }

        @ExceptionHandler(BadCredentialsException.class)
        @ResponseStatus(HttpStatus.UNAUTHORIZED)
        public ResponseEntity<ErrorResponse> handleBadCredentials(
                        BadCredentialsException ex, WebRequest request) {

                log.warn("Intento de autenticacion fallido: {}", ex.getMessage());

                ErrorResponse error = new ErrorResponse(
                                LocalDateTime.now(),
                                HttpStatus.UNAUTHORIZED.value(),
                                "No autorizado",
                                "Credenciales invalidas",
                                request.getDescription(false).replace("uri=", ""));

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        @ExceptionHandler(AccessDeniedException.class)
        @ResponseStatus(HttpStatus.FORBIDDEN)
        public ResponseEntity<ErrorResponse> handleAccessDenied(
                        AccessDeniedException ex, WebRequest request) {

                log.warn("Acceso denegado: {}", ex.getMessage());

                ErrorResponse error = new ErrorResponse(
                                LocalDateTime.now(),
                                HttpStatus.FORBIDDEN.value(),
                                "Prohibido",
                                "No tiene permisos para acceder a este recurso",
                                request.getDescription(false).replace("uri=", ""));

                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }

        @ExceptionHandler(RecursoNoEncontradoException.class)
        @ResponseStatus(HttpStatus.NOT_FOUND)
        public ResponseEntity<ErrorResponse> handleRecursoNoEncontrado(
                        RecursoNoEncontradoException ex, WebRequest request) {

                log.warn("Recurso no encontrado: {}", ex.getMessage());

                ErrorResponse error = new ErrorResponse(
                                LocalDateTime.now(),
                                HttpStatus.NOT_FOUND.value(),
                                "No encontrado",
                                ex.getMessage(),
                                request.getDescription(false).replace("uri=", ""));

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        @ExceptionHandler({ EmailYaRegistradoException.class })
        @ResponseStatus(HttpStatus.CONFLICT)
        public ResponseEntity<ErrorResponse> handleConflict(
                        RuntimeException ex, WebRequest request) {

                log.warn("Conflicto: {}", ex.getMessage());

                ErrorResponse error = new ErrorResponse(
                                LocalDateTime.now(),
                                HttpStatus.CONFLICT.value(),
                                "Conflicto",
                                ex.getMessage(),
                                request.getDescription(false).replace("uri=", ""));

                return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }

        @ExceptionHandler({
                        PasswordInvalidoException.class,
                        RegistroException.class,
                        CredencialesInvalidasException.class
        })
        @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
        public ResponseEntity<ErrorResponse> handleBusinessErrors(
                        RuntimeException ex, WebRequest request) {

                log.warn("Error de negocio: {}", ex.getMessage());

                ErrorResponse error = new ErrorResponse(
                                LocalDateTime.now(),
                                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                                "Error de procesamiento",
                                ex.getMessage(),
                                request.getDescription(false).replace("uri=", ""));

                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
        }

        @ExceptionHandler(RuntimeException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public ResponseEntity<ErrorResponse> handleRuntimeException(
                        RuntimeException ex, WebRequest request) {

                // Filtramos para no atrapar excepciones que ya tienen su propio handler
                if (ex.getClass() != RuntimeException.class) {
                    throw ex; 
                }

                log.warn("Excepción en tiempo de ejecución: {}", ex.getMessage());

                ErrorResponse error = new ErrorResponse(
                                LocalDateTime.now(),
                                HttpStatus.BAD_REQUEST.value(),
                                "Error en la solicitud",
                                ex.getMessage(),
                                request.getDescription(false).replace("uri=", ""));

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
        
        @ExceptionHandler(Exception.class)
        public ResponseEntity<?> handleAllExceptions(Exception ex, WebRequest request) {
                String path = request.getDescription(false).replace("uri=", "");

                if (!path.startsWith("/api")) {
                        return null;
                }

                log.error("Error interno no esperado en {}: {}", path, ex.getMessage(), ex);

                ErrorResponse error = new ErrorResponse(
                                LocalDateTime.now(),
                                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                "Error interno",
                                "Ha ocurrido un error interno. Por favor intente mas tarde.",
                                path);

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
}