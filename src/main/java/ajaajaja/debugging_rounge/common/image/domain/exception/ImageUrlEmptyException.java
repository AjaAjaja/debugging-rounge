package ajaajaja.debugging_rounge.common.image.domain.exception;

import ajaajaja.debugging_rounge.common.exception.BusinessException;
import ajaajaja.debugging_rounge.common.exception.ErrorCode;

public class ImageUrlEmptyException extends BusinessException {
    public ImageUrlEmptyException() {
        super(ErrorCode.IMAGE_URL_EMPTY);
    }
}



