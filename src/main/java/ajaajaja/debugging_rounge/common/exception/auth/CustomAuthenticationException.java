package ajaajaja.debugging_rounge.common.exception.auth;

import ajaajaja.debugging_rounge.common.exception.ErrorCode;
import org.springframework.security.authentication.InsufficientAuthenticationException;

public class CustomAuthenticationException extends InsufficientAuthenticationException {
    private final ErrorCode errorCode;
    public CustomAuthenticationException(ErrorCode errorCode) {
        super(errorCode.name());
        this.errorCode = errorCode;
    }
}
