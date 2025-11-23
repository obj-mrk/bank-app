package mrk.common.errors.impl;

import mrk.common.errors.ErrorCode;
import mrk.common.errors.ErrorException;
import org.springframework.http.HttpStatus;

public class CreditLimitExceededException extends ErrorException {
    public CreditLimitExceededException(String message) {
        super(ErrorCode.LIMIT_EXCEEDED, message, HttpStatus.BAD_REQUEST);
    }
}
