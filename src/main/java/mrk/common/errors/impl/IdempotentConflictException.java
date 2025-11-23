package mrk.common.errors.impl;

import mrk.common.errors.ErrorCode;
import mrk.common.errors.ErrorException;
import org.springframework.http.HttpStatus;

public class IdempotentConflictException extends ErrorException {
    public IdempotentConflictException(String message) {
        super(ErrorCode.IDEMPOTENT_CONFLICT, message, HttpStatus.CONFLICT);
    }
}