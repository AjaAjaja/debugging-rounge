package ajaajaja.debugging_rounge.common.exception.auth;

import ajaajaja.debugging_rounge.common.exception.ErrorCode;

public class UserIdentifierInvalidException extends CustomAuthenticationException{
    public UserIdentifierInvalidException() {
        super(ErrorCode.USER_IDENTIFIER_INVALID);
    }
}
