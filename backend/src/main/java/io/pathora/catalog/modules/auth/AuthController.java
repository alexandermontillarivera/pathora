package io.pathora.catalog.modules.auth;

import io.pathora.catalog.shared.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
@Tag(name = "Autenticación", description = "Registro, inicio de sesión y perfil de usuario")
public class AuthController {
  private final AuthService service;

  public AuthController(AuthService service) {
    this.service = service;
  }

  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Registrar un usuario")
  @SuppressWarnings("unused")
  ApiResponse<AuthDto.AuthResponse> register(@Valid @RequestBody AuthDto.RegisterRequest request) {
    return ApiResponse.ok("Cuenta creada correctamente.", service.register(request));
  }

  @PostMapping("/login")
  @Operation(summary = "Iniciar sesión")
  @SuppressWarnings("unused")
  ApiResponse<AuthDto.AuthResponse> login(@Valid @RequestBody AuthDto.LoginRequest request) {
    return ApiResponse.ok("Inicio de sesión correcto.", service.login(request));
  }

  @PostMapping("/refresh")
  @Operation(summary = "Renovar los tokens de una sesión")
  ApiResponse<AuthDto.AuthResponse> refresh(@Valid @RequestBody AuthDto.RefreshRequest request) {
    return ApiResponse.ok("Sesión renovada correctamente.", service.refresh(request));
  }

  @PostMapping("/logout")
  @Operation(summary = "Cerrar una sesión y revocar su token de renovación")
  ApiResponse<Void> logout(@Valid @RequestBody AuthDto.RefreshRequest request) {
    service.logout(request);
    return ApiResponse.ok("Sesión cerrada correctamente.", null);
  }

  @PostMapping("/forgot-password")
  @Operation(summary = "Solicitar un enlace para restablecer la contraseña")
  ApiResponse<Void> forgotPassword(@Valid @RequestBody AuthDto.ForgotPasswordRequest request) {
    service.forgotPassword(request);
    return ApiResponse.ok(
        "Si la cuenta existe, se ha enviado un enlace para restablecer la contraseña.", null);
  }

  @PostMapping("/reset-password")
  @Operation(summary = "Restablecer la contraseña con un token de un solo uso")
  ApiResponse<Void> resetPassword(@Valid @RequestBody AuthDto.ResetPasswordRequest request) {
    service.resetPassword(request);
    return ApiResponse.ok("Contraseña actualizada correctamente.", null);
  }

  @GetMapping("/me")
  @Operation(summary = "Obtener el usuario autenticado")
  @SecurityRequirement(name = "bearerAuth")
  @SuppressWarnings("unused")
  ApiResponse<AuthDto.UserResponse> me(@AuthenticationPrincipal Jwt jwt) {
    return ApiResponse.ok(
        "Usuario obtenido correctamente.", service.me(Long.valueOf(jwt.getSubject())));
  }
}
