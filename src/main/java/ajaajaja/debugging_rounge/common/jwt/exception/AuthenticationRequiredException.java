package ajaajaja.debugging_rounge.common.jwt.exception;

import ajaajaja.debugging_rounge.common.exception.ErrorCode;

public class AuthenticationRequiredException extends CustomAuthenticationException{
    public AuthenticationRequiredException() {
        super(ErrorCode.AUTHENTICATION_REQUIRED);
    }
}
