package mrk.common.errors.impl;

import mrk.common.errors.ErrorCode;
import mrk.common.errors.ErrorException;
import org.springframework.http.HttpStatus;

public class InvalidTransactionException extends ErrorException {
    public InvalidTransactionException(String message) {
        super(ErrorCode.INVALID_TRANSACTION, message, HttpStatus.BAD_REQUEST);
    }
}
