package ajaajaja.debugging_rounge.support;

import ajaajaja.debugging_rounge.common.config.security.SecurityConfig;
import ajaajaja.debugging_rounge.common.jwt.JwtConfig;
import ajaajaja.debugging_rounge.common.security.annotation.CurrentUserIdArgumentResolver;
import ajaajaja.debugging_rounge.common.security.handler.CustomAccessDeniedHandler;
import ajaajaja.debugging_rounge.common.security.handler.CustomAuthenticationEntryPoint;
import ajaajaja.debugging_rounge.common.security.token.CustomBearerTokenResolver;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@WebMvcTest
@Import({
        JwtConfig.class,
        SecurityConfig.class,
        CustomBearerTokenResolver.class,
        CustomAuthenticationEntryPoint.class,
        CustomAccessDeniedHandler.class,
        CurrentUserIdArgumentResolver.class
})
@ActiveProfiles("test")
public @interface WebMvcSecurityTest {
    
    @AliasFor(annotation = WebMvcTest.class, attribute = "value")
    Class<?>[] value() default {};
}