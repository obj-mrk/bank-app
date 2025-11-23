package mrk.common.errors.impl;

import mrk.common.errors.ErrorCode;
import mrk.common.errors.ErrorException;
import org.springframework.http.HttpStatus;

public class AccountFrozenException extends ErrorException {
    public AccountFrozenException(String message) {
        super(ErrorCode.ACCOUNT_FROZEN, message, HttpStatus.BAD_REQUEST);
    }
}
