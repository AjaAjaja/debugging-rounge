package ajaajaja.debugging_rounge.feature.auth.application.port.in;

import ajaajaja.debugging_rounge.feature.auth.application.dto.TokenPair;

public interface IssueTokensUseCase {
    TokenPair issueTokens(Long userId);
}
