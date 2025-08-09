package ajaajaja.debuging_rounge.global.auth.exception;

import ajaajaja.debuging_rounge.global.exception.BusinessException;
import ajaajaja.debuging_rounge.global.exception.ErrorCode;

public class UnsupportedSocialTypeException extends BusinessException {
    public UnsupportedSocialTypeException() {
        super(ErrorCode.UNSUPPORTED_SOCIAL_TYPE);
    }
}
