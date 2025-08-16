package ajaajaja.debugging_rounge.feature.auth.api.exception;

import ajaajaja.debugging_rounge.common.exception.BusinessException;
import ajaajaja.debugging_rounge.common.exception.ErrorCode;

public class JwtParsingException extends BusinessException {
    public JwtParsingException() {
        super(ErrorCode.JWT_PARSING_FAILED);
    }
}
