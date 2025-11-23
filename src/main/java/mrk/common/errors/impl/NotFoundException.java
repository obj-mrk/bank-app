package mrk.common.errors.impl;

import mrk.common.errors.ErrorCode;
import mrk.common.errors.ErrorException;
import org.springframework.http.HttpStatus;

public class NotFoundException extends ErrorException {
    public NotFoundException(String message) {
        super(ErrorCode.NOT_FOUND, message, HttpStatus.NOT_FOUND);
    }
}
