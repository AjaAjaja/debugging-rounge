package ajaajaja.debuging_rounge.global.exception;

public class JwtValidException extends BusinessException {

    public JwtValidException() {
        super(ErrorCode.JWT_INVALID);
    }
}
