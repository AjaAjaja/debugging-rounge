package ajaajaja.debugging_rounge.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class ErrorResponse {

    private final int status;

    private final String code;

    private final List<String> errorMessages;

    private final LocalDateTime timestamp;

    public static ErrorResponse of(ErrorCode errorCode, List<String> errorMessages) {
        return new ErrorResponse(
                errorCode.getHttpStatus().value(),
                errorCode.name(),
                errorMessages,
                LocalDateTime.now());
    }

}
