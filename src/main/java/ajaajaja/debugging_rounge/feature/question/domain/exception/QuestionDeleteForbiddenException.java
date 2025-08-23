package ajaajaja.debugging_rounge.feature.question.domain.exception;

import ajaajaja.debugging_rounge.common.exception.ErrorCode;
import ajaajaja.debugging_rounge.common.jwt.exception.CustomAuthorizationException;

public class QuestionDeleteForbiddenException extends CustomAuthorizationException {
    public QuestionDeleteForbiddenException() {
        super(ErrorCode.QUESTION_DELETE_FORBIDDEN);
    }
}
