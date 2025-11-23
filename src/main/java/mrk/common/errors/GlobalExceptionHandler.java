package mrk.common.errors;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ---------- Вспомогательное ----------

    private String currentTraceId() {
        String traceId = MDC.get("traceId");
        return traceId != null ? traceId : UUID.randomUUID().toString();
    }

    private ResponseEntity<ErrorResponse> buildResponse(
            ErrorCode code,
            String message,
            HttpStatus status,
            Map<String, Object> details
    ) {
        String traceId = currentTraceId();
        ErrorResponse body = ErrorResponse.of(traceId, code, message, details);
        return ResponseEntity.status(status).body(body);
    }

    // ---------- Бизнес-исключения ----------

    @ExceptionHandler(ErrorException.class)
    public ResponseEntity<ErrorResponse> handleErrorException(ErrorException ex, WebRequest request) {
        return buildResponse(
                ex.getCode(),
                ex.getMessage(),
                ex.getHttpStatus(),
                ex.getDetails()
        );
    }

    // ---------- Ошибки валидации DTO (@Valid в контроллерах) ----------

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                      WebRequest request) {

        Map<String, Object> details = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            details.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return buildResponse(
                ErrorCode.VALIDATION_ERROR,
                "Validation failed",
                HttpStatus.BAD_REQUEST,
                details
        );
    }

    @ExceptionHandler({BindException.class, ConstraintViolationException.class})
    public ResponseEntity<ErrorResponse> handleConstraintViolation(Exception ex, WebRequest request) {

        Map<String, Object> details = new HashMap<>();

        if (ex instanceof BindException bindEx) {
            for (FieldError fieldError : bindEx.getBindingResult().getFieldErrors()) {
                details.put(fieldError.getField(), fieldError.getDefaultMessage());
            }
        } else if (ex instanceof ConstraintViolationException violationEx) {
            for (ConstraintViolation<?> violation : violationEx.getConstraintViolations()) {
                details.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
        }

        return buildResponse(
                ErrorCode.VALIDATION_ERROR,
                "Validation failed",
                HttpStatus.BAD_REQUEST,
                details
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                      WebRequest request) {

        return buildResponse(
                ErrorCode.VALIDATION_ERROR,
                "Malformed JSON request",
                HttpStatus.BAD_REQUEST,
                Map.of("cause", ex.getMostSpecificCause().getMessage())
        );
    }

    // ---------- Security ----------

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(AuthenticationException ex, WebRequest request) {
        return buildResponse(
                ErrorCode.UNAUTHORIZED,
                ex.getMessage(),
                HttpStatus.UNAUTHORIZED,
                Map.of()
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, WebRequest request) {
        return buildResponse(
                ErrorCode.FORBIDDEN,
                ex.getMessage(),
                HttpStatus.FORBIDDEN,
                Map.of()
        );
    }

    // ---------- Fallback (непредвиденные ошибки) ----------

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, WebRequest request) {

        return buildResponse(
                ErrorCode.INTERNAL_ERROR,
                "Internal server error",
                HttpStatus.INTERNAL_SERVER_ERROR,
                Map.of("exception", ex.getClass().getSimpleName())
        );
    }
}
