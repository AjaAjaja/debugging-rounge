package ajaajaja.debugging_rounge.feature.answer.domain.exception;

import ajaajaja.debugging_rounge.common.exception.ErrorCode;
import ajaajaja.debugging_rounge.common.jwt.exception.CustomAuthorizationException;

public class AnswerUpdateForbiddenException extends CustomAuthorizationException {
    public AnswerUpdateForbiddenException() {
        super(ErrorCode.ANSWER_UPDATE_FORBIDDEN);
    }
}
