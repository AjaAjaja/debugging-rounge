package ajaajaja.debugging_rounge.common.image.domain.exception;

import ajaajaja.debugging_rounge.common.exception.BusinessException;
import ajaajaja.debugging_rounge.common.exception.ErrorCode;

public class ImageUrlInvalidFormatException extends BusinessException {
    public ImageUrlInvalidFormatException() {
        super(ErrorCode.IMAGE_URL_INVALID_FORMAT);
    }
}



