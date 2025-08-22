package ajaajaja.debugging_rounge.common.exception.auth;

import ajaajaja.debugging_rounge.common.exception.BusinessException;
import ajaajaja.debugging_rounge.common.exception.ErrorCode;

public class CustomAuthorizationException extends BusinessException {
    public CustomAuthorizationException(ErrorCode errorCode) {
        super(errorCode);
    }
}
