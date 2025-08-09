package ajaajaja.debuging_rounge.domain.user.exception;

import ajaajaja.debuging_rounge.global.exception.BusinessException;
import ajaajaja.debuging_rounge.global.exception.ErrorCode;

public class UserNotFoundException extends BusinessException {
    public UserNotFoundException() {
        super(ErrorCode.USER_NOT_FOUND);
    }
}
