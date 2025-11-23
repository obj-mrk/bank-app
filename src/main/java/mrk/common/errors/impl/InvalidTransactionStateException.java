package mrk.common.errors.impl;

import mrk.common.errors.ErrorCode;
import mrk.common.errors.ErrorException;
import org.springframework.http.HttpStatus;

public class InvalidTransactionStateException extends ErrorException {
    public InvalidTransactionStateException(String message) {
        super(ErrorCode.INVALID_TRANSACTION_STATE, message, HttpStatus.BAD_REQUEST);
    }
}
