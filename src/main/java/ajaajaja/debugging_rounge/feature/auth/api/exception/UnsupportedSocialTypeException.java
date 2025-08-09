package ajaajaja.debugging_rounge.feature.auth.api.exception;

import ajaajaja.debugging_rounge.common.exception.BusinessException;
import ajaajaja.debugging_rounge.common.exception.ErrorCode;

public class UnsupportedSocialTypeException extends BusinessException {
    public UnsupportedSocialTypeException() {
        super(ErrorCode.UNSUPPORTED_SOCIAL_TYPE);
    }
}
