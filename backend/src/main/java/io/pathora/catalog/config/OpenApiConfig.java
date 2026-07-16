package io.pathora.catalog.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
  @Bean
  @SuppressWarnings("unused")
  OpenAPI careersOpenApi() {
    Info info =
        new Info()
            .title("API del catálogo académico de Pathora")
            .version("1.0.0")
            .description("Gestión del catálogo académico de escuelas y carreras.")
            .contact(new Contact().name("Pathora"));

    return new OpenAPI()
        .components(
            new Components()
                .addSecuritySchemes(
                    "bearerAuth",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")))
        .info(info);
  }
}
