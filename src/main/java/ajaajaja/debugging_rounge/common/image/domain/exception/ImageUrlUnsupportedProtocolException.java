package ajaajaja.debugging_rounge.common.image.domain.exception;

import ajaajaja.debugging_rounge.common.exception.BusinessException;
import ajaajaja.debugging_rounge.common.exception.ErrorCode;

public class ImageUrlUnsupportedProtocolException extends BusinessException {
    public ImageUrlUnsupportedProtocolException() {
        super(ErrorCode.IMAGE_URL_UNSUPPORTED_PROTOCOL);
    }
}



