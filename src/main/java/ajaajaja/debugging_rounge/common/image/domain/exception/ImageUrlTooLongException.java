package ajaajaja.debugging_rounge.common.image.domain.exception;

import ajaajaja.debugging_rounge.common.exception.BusinessException;
import ajaajaja.debugging_rounge.common.exception.ErrorCode;

public class ImageUrlTooLongException extends BusinessException {
    public ImageUrlTooLongException() {
        super(ErrorCode.IMAGE_URL_TOO_LONG);
    }
}



