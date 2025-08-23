package ajaajaja.debugging_rounge.common.jwt.exception;

import ajaajaja.debugging_rounge.common.exception.ErrorCode;

public class JwtParsingException extends CustomAuthenticationException {
    public JwtParsingException() {
        super(ErrorCode.JWT_PARSING_FAILED);
    }
}
