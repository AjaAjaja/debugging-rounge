package ajaajaja.debugging_rounge.feature.auth.api.exception;

import ajaajaja.debugging_rounge.common.exception.BusinessException;
import ajaajaja.debugging_rounge.common.exception.ErrorCode;

public class JwtValidationException extends BusinessException {

    public JwtValidationException() {
        super(ErrorCode.JWT_INVALID);
    }
}
