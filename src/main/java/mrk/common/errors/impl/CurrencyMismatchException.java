package mrk.common.errors.impl;

import mrk.common.errors.ErrorCode;
import mrk.common.errors.ErrorException;
import org.springframework.http.HttpStatus;

public class CurrencyMismatchException extends ErrorException {

    public CurrencyMismatchException(String message) {
        super(ErrorCode.VALIDATION_ERROR, message, HttpStatus.BAD_REQUEST);
    }
}
