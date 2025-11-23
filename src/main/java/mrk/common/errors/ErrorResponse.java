package mrk.common.errors;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse(
        Instant timestamp,
        String traceId,
        String error,          // строковое представление ErrorCode
        String message,
        Map<String, Object> details
) {
    public static ErrorResponse of(String traceId, ErrorCode code, String message, Map<String, Object> details) {
        return new ErrorResponse(
                Instant.now(),
                traceId,
                code.name(),
                message,
                details == null ? Map.of() : Map.copyOf(details)
        );
    }

    public static ErrorResponse of(String traceId, ErrorCode code, String message) {
        return of(traceId, code, message, Map.of());
    }
}
