package ajaajaja.debugging_rounge.feature.auth.api.exception;

import ajaajaja.debugging_rounge.common.exception.BusinessException;
import ajaajaja.debugging_rounge.common.exception.ErrorCode;

public class JwtCreationException extends BusinessException {
    public JwtCreationException() {
        super(ErrorCode.JWT_CREATION_FAILED);
    }
}
