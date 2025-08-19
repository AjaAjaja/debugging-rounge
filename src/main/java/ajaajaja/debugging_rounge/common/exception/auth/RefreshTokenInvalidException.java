package ajaajaja.debugging_rounge.common.exception.auth;

import ajaajaja.debugging_rounge.common.exception.ErrorCode;
import ajaajaja.debugging_rounge.common.exception.auth.CustomAuthenticationException;

public class RefreshTokenInvalidException extends CustomAuthenticationException {
    public RefreshTokenInvalidException() {
        super(ErrorCode.REFRESH_TOKEN_INVALID);
    }
}
