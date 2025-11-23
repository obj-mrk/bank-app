package mrk.common.errors;

import org.springframework.http.HttpStatus;

import java.util.Map;

public abstract class ErrorException extends RuntimeException {
    private final ErrorCode code;
    private final HttpStatus httpStatus;
    private final Map<String, Object> details;

    protected ErrorException(ErrorCode code, String message, HttpStatus status) {
        this(code, message, status, Map.of());
    }

    protected ErrorException(ErrorCode code, String message, HttpStatus status, Map<String, Object> details) {
        super(message);
        this.code = code;
        this.httpStatus = status;
        this.details = details;
    }

    public ErrorCode getCode() { return code; }
    public HttpStatus getHttpStatus() { return httpStatus; }
    public Map<String, Object> getDetails() { return details; }
}
