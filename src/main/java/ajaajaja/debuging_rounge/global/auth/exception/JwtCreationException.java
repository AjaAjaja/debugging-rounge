package ajaajaja.debuging_rounge.global.auth.exception;

import ajaajaja.debuging_rounge.global.exception.BusinessException;
import ajaajaja.debuging_rounge.global.exception.ErrorCode;

public class JwtCreationException extends BusinessException {
    public JwtCreationException() {
        super(ErrorCode.JWT_CREATION_FAILED);
    }
}
