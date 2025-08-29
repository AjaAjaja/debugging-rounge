package ajaajaja.debugging_rounge.feature.user.application.port.out;

import ajaajaja.debugging_rounge.feature.user.domain.model.User;

public interface SaveUserPort {
    User save(User user);
}
