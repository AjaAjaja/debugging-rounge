package ajaajaja.debugging_rounge.common.exception.auth;

import ajaajaja.debugging_rounge.common.exception.ErrorCode;
import ajaajaja.debugging_rounge.common.exception.auth.CustomAuthenticationException;

public class JwtValidationException extends CustomAuthenticationException {

    public JwtValidationException() {
        super(ErrorCode.JWT_INVALID);
    }
}
