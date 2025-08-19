package ajaajaja.debugging_rounge.common.exception.auth;

import ajaajaja.debugging_rounge.common.exception.ErrorCode;
import ajaajaja.debugging_rounge.common.exception.auth.CustomAuthenticationException;

public class JwtParsingException extends CustomAuthenticationException {
    public JwtParsingException() {
        super(ErrorCode.JWT_PARSING_FAILED);
    }
}
