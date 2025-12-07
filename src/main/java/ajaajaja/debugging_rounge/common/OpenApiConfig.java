package ajaajaja.debugging_rounge.common;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearer-jwt";
    private static final String COOKIE_SECURITY_SCHEME_NAME = "refresh-token-cookie";
    private static final Map<String, Integer> TAG_ORDER = Map.of(
            "Auth", 1,
            "User", 2,
            "Question", 3,
            "Question Recommend", 4,
            "Answer", 5,
            "Answer Recommend", 6
    );

    @Bean
    public OpenAPI debuggingRoungeOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Debugging Rounge API")
                        .description("Q&A 보드 서비스(Debugging Rounge)의 REST API 문서입니다.\n\n" +
                                "## 인증 방법\n\n" +
                                "### 1. Access Token 및 Refresh Token 획득\n" +
                                "구글 OAuth2 로그인을 통해 토큰을 획득합니다:\n" +
                                "- **로그인 성공 시:**\n" +
                                "  - Access Token: 응답 본문에 JSON 형식으로 반환\n" +
                                "  - Refresh Token: 쿠키(`refreshToken`)로 설정 (경로: `/auth`)\n\n" +
                                "### 2. Access Token 사용\n" +
                                "대부분의 API 호출 시 Access Token이 필요합니다:\n" +
                                "- **헤더 형식:** `Authorization: Bearer {accessToken}`\n" +
                                "- **Swagger UI에서:** 상단 'Authorize' 버튼 → `bearer-jwt`에 Access Token 입력\n\n" +
                                "참고로 Access Token은 개발자 도구의 Session Storage나 로그인 시 Response Body에서 확인할 수 있습니다.\n\n" +
                                "### 3. Refresh Token 사용\n" +
                                "토큰 재발급 및 로그아웃 시 Refresh Token이 필요합니다:\n" +
                                "- **쿠키 형식:** `refreshToken={refreshToken}` (경로: `/auth`)\n" +
                                "- **Swagger UI에서:** 상단 'Authorize' 버튼 → `refresh-token-cookie`에 Refresh Token 입력\n" +
                                "- **참고:** 브라우저 개발자 도구에서 쿠키를 확인할 때 경로가 `/auth`인 쿠키만 표시됩니다\n\n" +
                                "### 4. 자동 인증 (브라우저 쿠키 활용)\n" +
                                "**중요:** 브라우저에서 이미 로그인한 상태라면, Swagger UI에서 별도로 토큰을 입력하지 않아도 됩니다:\n" +
                                "- 브라우저에 저장된 쿠키(`refreshToken`)가 자동으로 전달됩니다\n" +
                                "- 로그아웃(`POST /auth/logout`) 및 토큰 재발급(`POST /auth/refresh`) API는 쿠키가 있으면 바로 'Try it out'으로 테스트 가능합니다\n" +
                                "- Access Token이 필요한 API는 여전히 'Authorize' 버튼에서 토큰을 입력해야 합니다\n\n"
                        )
                        .version("v1"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Access Token을 Authorization 헤더에 Bearer 형식으로 전달"))
                        .addSecuritySchemes(COOKIE_SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(COOKIE_SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.COOKIE)
                                        .name("refreshToken")
                                        .description("Refresh Token을 쿠키(refreshToken)로 전달")));
    }

    @Bean
    public OpenApiCustomizer sortTagsByCustomOrder() {
        return openApi -> {
            List<Tag> tags = openApi.getTags();
            if (tags != null) {
                tags.sort(Comparator.comparing(tag -> 
                    TAG_ORDER.getOrDefault(tag.getName(), Integer.MAX_VALUE)
                ));
                openApi.setTags(tags);
            }
        };
    }
}
