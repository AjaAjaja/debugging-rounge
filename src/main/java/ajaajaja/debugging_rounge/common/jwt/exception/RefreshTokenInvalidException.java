package ajaajaja.debugging_rounge.common.jwt.exception;

import ajaajaja.debugging_rounge.common.exception.ErrorCode;

public class RefreshTokenInvalidException extends CustomAuthenticationException {
    public RefreshTokenInvalidException() {
        super(ErrorCode.REFRESH_TOKEN_INVALID);
    }
}
