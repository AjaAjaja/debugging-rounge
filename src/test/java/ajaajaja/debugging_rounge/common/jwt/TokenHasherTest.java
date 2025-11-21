package ajaajaja.debugging_rounge.common.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class TokenHasherTest {

    TokenHasher tokenHasher;
    byte[] rawPepperBytes;

    @BeforeEach
    void setUp() {

        String rawPepper = "01234567890123456789012345678901";
        rawPepperBytes = rawPepper.getBytes(StandardCharsets.UTF_8);

        String encodedPepper = Base64.getEncoder().encodeToString(rawPepperBytes);

        tokenHasher = new TokenHasher(encodedPepper);
    }

    @Test
    @DisplayName("같은 입력에 항상 같은 해쉬를 반환한다")
    void 같은_입력_항상_같은_해쉬() {

        // given
        String token = "this-is-json-web-token";

        // when
        byte[] hash1 = tokenHasher.hmacSha256(token);
        byte[] hash2 = tokenHasher.hmacSha256(token);

        // then
        assertThat(hash1).isEqualTo(hash2);
    }

    @Test
    @DisplayName("다른 입력은 항상 서로 다른 해쉬를 반환한다")
    void 다른_입력_항상_다른_해쉬() {

        // given
        String token = "this-is-json-web-token";
        String anotherToken = "this-is-another-json-web-token";

        // when
        byte[] tokenHash = tokenHasher.hmacSha256(token);
        byte[] anotherTokenHash = tokenHasher.hmacSha256(anotherToken);

        // then
        assertThat(tokenHash).isNotEqualTo(anotherTokenHash);
    }

    @Test
    @DisplayName("HmacSHA256 결과는 32바이트 길이를 가진다")
    void HmacSHA256은_항상_32바이트() {

        // given
        String token1 = "this-is-json-web-token1";
        String token2 = "this-is-json-web-token2";
        String token3 = "this-is-json-web-token3";
        String token4 = "this-is-json-web-token4";
        String token5 = "token5";


        // when
        byte[] hash1 = tokenHasher.hmacSha256(token1);
        byte[] hash2 = tokenHasher.hmacSha256(token2);
        byte[] hash3 = tokenHasher.hmacSha256(token3);
        byte[] hash4 = tokenHasher.hmacSha256(token4);
        byte[] hash5 = tokenHasher.hmacSha256(token5);

        // then
        assertThat(hash1).hasSize(32);
        assertThat(hash2).hasSize(32);
        assertThat(hash3).hasSize(32);
        assertThat(hash4).hasSize(32);
        assertThat(hash5).hasSize(32);
    }

    @Test
    @DisplayName("pepper는 Base64로 로딩 -> 디코딩되어 Hmac256으로 해쉬되어 key가 된다.")
    void base64_pepper_hmac256_key() throws Exception {

        // given
        String token = "this-is-json-web-token";
        byte[] actual = tokenHasher.hmacSha256(token);

        // when
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(rawPepperBytes, "HmacSHA256"));
        byte[] expected = mac.doFinal(token.getBytes(StandardCharsets.UTF_8));

        // then
        assertThat(actual).isEqualTo(expected);
    }
}