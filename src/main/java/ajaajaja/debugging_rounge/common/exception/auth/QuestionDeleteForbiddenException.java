package ajaajaja.debugging_rounge.common.exception.auth;

import ajaajaja.debugging_rounge.common.exception.ErrorCode;

public class QuestionDeleteForbiddenException extends CustomAuthorizationException{
    public QuestionDeleteForbiddenException() {
        super(ErrorCode.QUESTION_DELETE_FORBIDDEN);
    }
}
