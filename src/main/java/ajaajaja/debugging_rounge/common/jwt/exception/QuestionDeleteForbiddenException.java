package ajaajaja.debugging_rounge.common.jwt.exception;

import ajaajaja.debugging_rounge.common.exception.ErrorCode;

public class QuestionDeleteForbiddenException extends CustomAuthorizationException{
    public QuestionDeleteForbiddenException() {
        super(ErrorCode.QUESTION_DELETE_FORBIDDEN);
    }
}
