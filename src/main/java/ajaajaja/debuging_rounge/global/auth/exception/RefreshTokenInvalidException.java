package ajaajaja.debuging_rounge.global.auth.exception;

import ajaajaja.debuging_rounge.global.exception.BusinessException;
import ajaajaja.debuging_rounge.global.exception.ErrorCode;

public class RefreshTokenInvalidException extends BusinessException {
    public RefreshTokenInvalidException() {
        super(ErrorCode.REFRESH_TOKEN_INVALID);
    }
}
