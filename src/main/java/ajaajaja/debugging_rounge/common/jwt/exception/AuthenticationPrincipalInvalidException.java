package ajaajaja.debugging_rounge.common.jwt.exception;

import ajaajaja.debugging_rounge.common.exception.ErrorCode;

public class AuthenticationPrincipalInvalidException extends CustomAuthenticationException{
    public AuthenticationPrincipalInvalidException() {
        super(ErrorCode.AUTHENTICATION_PRINCIPAL_INVALID);
    }
}
