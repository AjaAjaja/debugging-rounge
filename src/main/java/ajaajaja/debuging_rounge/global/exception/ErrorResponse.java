package ajaajaja.debuging_rounge.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {

    private final int status;

    private final String errorMessage;

    private final String code;

    public static ErrorResponse of(ErrorCode errorCode, String errorMessage) {
        return new ErrorResponse(errorCode.getHttpStatus().value(), errorMessage, errorCode.name());
    }

}
