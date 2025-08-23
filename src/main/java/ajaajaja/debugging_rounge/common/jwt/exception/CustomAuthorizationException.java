package ajaajaja.debugging_rounge.common.jwt.exception;

import ajaajaja.debugging_rounge.common.exception.BusinessException;
import ajaajaja.debugging_rounge.common.exception.ErrorCode;

public class CustomAuthorizationException extends BusinessException {
    public CustomAuthorizationException(ErrorCode errorCode) {
        super(errorCode);
    }
}
