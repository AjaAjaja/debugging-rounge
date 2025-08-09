package ajaajaja.debugging_rounge.feature.auth.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TokenType {

    ACCESS("access"),
    REFRESH("refresh");

    private String type;
}
