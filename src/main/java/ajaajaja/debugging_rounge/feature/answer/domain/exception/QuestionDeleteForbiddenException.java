package ajaajaja.debugging_rounge.feature.answer.domain.exception;

import ajaajaja.debugging_rounge.common.exception.ErrorCode;
import ajaajaja.debugging_rounge.common.jwt.exception.CustomAuthorizationException;

public class QuestionDeleteForbiddenException extends CustomAuthorizationException {

    public QuestionDeleteForbiddenException() {
        super(ErrorCode.ANSWER_DELETE_FORBIDDEN);
    }
}
