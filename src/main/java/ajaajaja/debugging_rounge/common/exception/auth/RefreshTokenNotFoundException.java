package ajaajaja.debugging_rounge.common.exception.auth;

import ajaajaja.debugging_rounge.common.exception.ErrorCode;
import ajaajaja.debugging_rounge.common.exception.auth.CustomAuthenticationException;

public class RefreshTokenNotFoundException extends CustomAuthenticationException {
    public RefreshTokenNotFoundException() {
        super(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
    }
}
