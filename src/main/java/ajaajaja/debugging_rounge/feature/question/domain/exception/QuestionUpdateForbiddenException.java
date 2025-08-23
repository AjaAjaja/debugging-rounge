package ajaajaja.debugging_rounge.feature.question.domain.exception;

import ajaajaja.debugging_rounge.common.exception.ErrorCode;
import ajaajaja.debugging_rounge.common.jwt.exception.CustomAuthorizationException;

public class QuestionUpdateForbiddenException extends CustomAuthorizationException {
    public QuestionUpdateForbiddenException() {
        super(ErrorCode.QUESTION_UPDATE_FORBIDDEN);
    }
}
