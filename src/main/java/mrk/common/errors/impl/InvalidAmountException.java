package mrk.common.errors.impl;

import mrk.common.errors.ErrorCode;
import mrk.common.errors.ErrorException;
import org.springframework.http.HttpStatus;

public class InvalidAmountException extends ErrorException {
    public InvalidAmountException(String message) {
        super(ErrorCode.INVALID_AMOUNT, message,  HttpStatus.BAD_REQUEST);
    }
}
