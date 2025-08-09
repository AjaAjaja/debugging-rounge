package ajaajaja.debuging_rounge.global.auth.exception;

import ajaajaja.debuging_rounge.global.exception.BusinessException;
import ajaajaja.debuging_rounge.global.exception.ErrorCode;

public class JwtParsingException extends BusinessException {
    public JwtParsingException() {
        super(ErrorCode.JWT_PARSING_FAILED);
    }
}
