package ajaajaja.debugging_rounge.common.security.validator;

import ajaajaja.debugging_rounge.common.jwt.exception.CustomAuthorizationException;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.function.Supplier;

@Component
public class OwnershipValidator {

    public void validateAuthor(Long authorId, Long loginUserId, Supplier<? extends CustomAuthorizationException> ex) {
        if (!Objects.equals(authorId, loginUserId)) {
            throw ex.get();
        }
    }
}
