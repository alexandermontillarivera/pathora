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
@Tag(name = "Authentication", description = "User registration, login and profile")
public class AuthController {
  private final AuthService service;

  public AuthController(AuthService service) {
    this.service = service;
  }

  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Register a user")
  @SuppressWarnings("unused")
  ApiResponse<AuthDto.AuthResponse> register(@Valid @RequestBody AuthDto.RegisterRequest request) {
    return ApiResponse.ok("Account created.", service.register(request));
  }

  @PostMapping("/login")
  @Operation(summary = "Log in")
  @SuppressWarnings("unused")
  ApiResponse<AuthDto.AuthResponse> login(@Valid @RequestBody AuthDto.LoginRequest request) {
    return ApiResponse.ok("Login successful.", service.login(request));
  }

  @PostMapping("/refresh")
  @Operation(summary = "Rotate a refresh token and issue a new session pair")
  ApiResponse<AuthDto.AuthResponse> refresh(@Valid @RequestBody AuthDto.RefreshRequest request) {
    return ApiResponse.ok("Session refreshed.", service.refresh(request));
  }

  @PostMapping("/logout")
  @Operation(summary = "Revoke a refresh token")
  ApiResponse<Void> logout(@Valid @RequestBody AuthDto.RefreshRequest request) {
    service.logout(request);
    return ApiResponse.ok("Session closed.", null);
  }

  @PostMapping("/forgot-password")
  @Operation(summary = "Request a password reset link")
  ApiResponse<Void> forgotPassword(@Valid @RequestBody AuthDto.ForgotPasswordRequest request) {
    service.forgotPassword(request);
    return ApiResponse.ok("If the account exists, a reset link has been sent.", null);
  }

  @PostMapping("/reset-password")
  @Operation(summary = "Reset password with a one-time token")
  ApiResponse<Void> resetPassword(@Valid @RequestBody AuthDto.ResetPasswordRequest request) {
    service.resetPassword(request);
    return ApiResponse.ok("Password updated.", null);
  }

  @GetMapping("/me")
  @Operation(summary = "Get authenticated user")
  @SecurityRequirement(name = "bearerAuth")
  @SuppressWarnings("unused")
  ApiResponse<AuthDto.UserResponse> me(@AuthenticationPrincipal Jwt jwt) {
    return ApiResponse.ok("User retrieved.", service.me(Long.valueOf(jwt.getSubject())));
  }
}
