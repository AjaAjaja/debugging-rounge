package ajaajaja.debugging_rounge.common.image.domain.exception;

import ajaajaja.debugging_rounge.common.exception.BusinessException;
import ajaajaja.debugging_rounge.common.exception.ErrorCode;

public class ImageUrlDomainNotAllowedException extends BusinessException {
    public ImageUrlDomainNotAllowedException() {
        super(ErrorCode.IMAGE_URL_DOMAIN_NOT_ALLOWED);
    }
}



