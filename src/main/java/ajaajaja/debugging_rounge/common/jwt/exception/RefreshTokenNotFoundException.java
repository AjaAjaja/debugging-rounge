package ajaajaja.debugging_rounge.common.jwt.exception;

import ajaajaja.debugging_rounge.common.exception.ErrorCode;

public class RefreshTokenNotFoundException extends CustomAuthenticationException {
    public RefreshTokenNotFoundException() {
        super(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
    }
}
