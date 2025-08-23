package ajaajaja.debugging_rounge.common.jwt.exception;

import ajaajaja.debugging_rounge.common.exception.ErrorCode;

public class JwtValidationException extends CustomAuthenticationException {

    public JwtValidationException() {
        super(ErrorCode.JWT_INVALID);
    }
}
