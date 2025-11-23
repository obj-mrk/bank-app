package mrk.common.errors.impl;

import mrk.common.errors.ErrorCode;
import mrk.common.errors.ErrorException;
import org.springframework.http.HttpStatus;

public class InvalidAccountException extends ErrorException {
    public InvalidAccountException(String message) {
        super(ErrorCode.INVALID_ACCOUNT, message, HttpStatus.BAD_REQUEST);
    }
}
