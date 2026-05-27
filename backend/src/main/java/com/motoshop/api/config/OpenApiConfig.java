package com.motoshop.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 * OpenAPI 3 metadata and global security scheme. The "bearer-jwt" scheme
 * is referenced from controllers via {@code @SecurityRequirement} or made
 * global here so Swagger UI shows the "Authorize" button.
 * <p>
 * Public endpoints (login, register, catalog reads, /actuator/health)
 * still render without the lock icon because we attach the requirement
 * only to operations that actually need it via controller-level
 * annotations. Declaring the scheme here is enough to enable the UI;
 * we keep the security requirement opt-in.
 */
@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearer-jwt";

    @Bean
    public OpenAPI motoshopOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MotoShop API")
                        .description("""
                                REST API of the MotoShop ecommerce platform.

                                Authentication: obtain a JWT through `POST /api/auth/login` and
                                paste it into the "Authorize" dialog above. The token is sent as
                                `Authorization: Bearer <token>` on every protected request.
                                """)
                        .version("0.1.0")
                        .contact(new Contact()
                                .name("MotoShop — TFM")
                                .url("https://github.com/"))
                        .license(new License().name("MIT")))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Paste a JWT issued by /api/auth/login")))
                // Apply the scheme globally so the UI shows a lock on every
                // endpoint by default; springdoc skips it on operations
                // matched by the security filter chain's permitAll() rules.
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME));
    }
}