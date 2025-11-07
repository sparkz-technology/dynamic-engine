package com.io.sdk.dynamic_engine.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

        @Bean
        public OpenAPI apiInfo() {

                // ✅ Define JWT Bearer Scheme
                SecurityScheme bearerScheme = new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .name("Authorization")
                                .in(SecurityScheme.In.HEADER);

                // ✅ Add security to all protected routes
                SecurityRequirement securityRequirement = new SecurityRequirement().addList("bearerAuth");

                return new OpenAPI()
                                .info(new Info()
                                                .title("Dynamic Engine API")
                                                .description("Enterprise-grade dynamic SDK backend")
                                                .version("1.0.0")
                                                .contact(new Contact()
                                                                .name("Your Company")
                                                                .email("support@yourcompany.com")
                                                                .url("https://yourcompany.com")))
                                .schemaRequirement("bearerAuth", bearerScheme)
                                .addSecurityItem(securityRequirement); // ✅ activate globally
        }
}
