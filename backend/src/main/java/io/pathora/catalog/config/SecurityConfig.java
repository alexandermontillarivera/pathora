package io.pathora.catalog.config;

import io.pathora.catalog.shared.exception.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class SecurityConfig {
  private static final ObjectMapper JSON = new ObjectMapper();

  @Bean
  @SuppressWarnings("unused")
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http.csrf(csrf -> csrf.disable())
        .cors(Customizer.withDefaults())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .exceptionHandling(
            exceptions ->
                exceptions
                    .authenticationEntryPoint(
                        (request, response, exception) ->
                            writeSecurityError(
                                request,
                                response,
                                HttpServletResponse.SC_UNAUTHORIZED,
                                "No autorizado",
                                "Debes iniciar sesión para realizar esta acción."))
                    .accessDeniedHandler(
                        (request, response, exception) ->
                            writeSecurityError(
                                request,
                                response,
                                HttpServletResponse.SC_FORBIDDEN,
                                "Acceso prohibido",
                                "No tienes permisos para realizar esta acción.")))
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(
                        "/v1/auth/register",
                        "/v1/auth/login",
                        "/v1/auth/refresh",
                        "/v1/auth/logout",
                        "/v1/auth/forgot-password",
                        "/v1/auth/reset-password",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**",
                        "/actuator/health")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/v1/careers/*/ratings/me")
                    .authenticated()
                    .requestMatchers(
                        HttpMethod.GET,
                        "/v1/schools/**",
                        "/v1/careers/**",
                        "/v1/pensums/**",
                        "/v1/ratings/**",
                        "/v1/comments/**")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/v1/community/**")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/v1/users/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .oauth2ResourceServer(
            oauth ->
                oauth
                    .jwt(Customizer.withDefaults())
                    .authenticationEntryPoint(
                        (request, response, exception) ->
                            writeSecurityError(
                                request,
                                response,
                                HttpServletResponse.SC_UNAUTHORIZED,
                                "No autorizado",
                                "La sesión no es válida o ha expirado.")))
        .build();
  }

  @Bean
  @SuppressWarnings("unused")
  PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  @Bean
  @SuppressWarnings("unused")
  JwtEncoder jwtEncoder(@Value("${app.jwt.secret}") String secret) {
    return NimbusJwtEncoder.withSecretKey(secretKey(secret)).algorithm(MacAlgorithm.HS256).build();
  }

  @Bean
  @SuppressWarnings("unused")
  JwtDecoder jwtDecoder(@Value("${app.jwt.secret}") String secret) {
    return NimbusJwtDecoder.withSecretKey(secretKey(secret))
        .macAlgorithm(MacAlgorithm.HS256)
        .build();
  }

  @Bean
  @SuppressWarnings("unused")
  CorsConfigurationSource corsConfigurationSource(
      @Value("${app.cors.allowed-origins}") String origins) {
    var configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.stream(origins.split(",")).map(String::trim).toList());
    configuration.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(java.util.List.of("Authorization", "Content-Type"));
    var source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  private SecretKey secretKey(String encodedSecret) {
    var bytes = java.util.Base64.getDecoder().decode(encodedSecret);
    if (bytes.length < 32)
      throw new IllegalStateException("JWT_SECRET must contain at least 256 bits.");
    return new SecretKeySpec(bytes, "HmacSHA256");
  }

  private static void writeSecurityError(
      HttpServletRequest request,
      HttpServletResponse response,
      int status,
      String error,
      String message)
      throws IOException {
    response.setStatus(status);
    response.setCharacterEncoding(java.nio.charset.StandardCharsets.UTF_8.name());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    JSON.writeValue(
        response.getWriter(),
        new ApiError(status, error, message, request.getRequestURI(), null, Instant.now()));
  }
}
