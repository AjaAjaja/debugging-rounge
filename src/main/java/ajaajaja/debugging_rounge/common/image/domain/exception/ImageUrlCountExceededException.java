package ajaajaja.debugging_rounge.common.image.domain.exception;

import ajaajaja.debugging_rounge.common.exception.BusinessException;
import ajaajaja.debugging_rounge.common.exception.ErrorCode;

public class ImageUrlCountExceededException extends BusinessException {
    public ImageUrlCountExceededException() {
        super(ErrorCode.IMAGE_URL_COUNT_EXCEEDED);
    }
}



