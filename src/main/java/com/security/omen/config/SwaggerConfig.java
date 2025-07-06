package com.security.omen.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.*;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("OAuth2 Secured API")
                        .version("1.0.0")
                        .description("API secured with OAuth2 Authorization Server"))
                .components(new Components()
                        .addSecuritySchemes("oauth2", new SecurityScheme()
                                .type(SecurityScheme.Type.OAUTH2)
                                .description("OAuth2 Login with Authorization Code")
                                .flows(new OAuthFlows()
                                        .authorizationCode(new OAuthFlow()
                                                .authorizationUrl("http://localhost:9000/oauth2/authorize")
                                                .tokenUrl("http://localhost:9000/oauth2/token")
                                                .scopes(new Scopes()
                                                        .addString("openid", "OpenID scope")
                                                        .addString("profile", "Profile scope")
                                                        .addString("email", "Email scope")
                                                )
                                        )
                                )
                        ))
                .addSecurityItem(new SecurityRequirement().addList("oauth2", List.of("openid", "profile", "email")));
    }
}
