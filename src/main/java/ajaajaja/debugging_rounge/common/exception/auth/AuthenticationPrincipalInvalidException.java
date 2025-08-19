package ajaajaja.debugging_rounge.common.exception.auth;

import ajaajaja.debugging_rounge.common.exception.ErrorCode;

public class AuthenticationPrincipalInvalidException extends CustomAuthenticationException{
    public AuthenticationPrincipalInvalidException() {
        super(ErrorCode.AUTHENTICATION_PRINCIPAL_INVALID);
    }
}
