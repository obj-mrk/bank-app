package mrk.common.errors.impl;

import mrk.common.errors.ErrorCode;
import mrk.common.errors.ErrorException;
import org.springframework.http.HttpStatus;

public class InvalidUserException extends ErrorException {
    public InvalidUserException(String message) {
        super(ErrorCode.INVALID_USER, message, HttpStatus.BAD_REQUEST);
    }
}
