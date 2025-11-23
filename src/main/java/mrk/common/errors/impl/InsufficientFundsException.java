package mrk.common.errors.impl;

import mrk.common.errors.ErrorCode;
import mrk.common.errors.ErrorException;
import org.springframework.http.HttpStatus;

public class InsufficientFundsException extends ErrorException {
    public InsufficientFundsException(String message) {
        super(ErrorCode.INSUFFICIENT_FUNDS, message, HttpStatus.BAD_REQUEST);
    }
}
