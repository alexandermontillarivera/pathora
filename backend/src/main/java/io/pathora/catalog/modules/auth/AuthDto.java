package io.pathora.catalog.modules.auth;

import io.pathora.catalog.entities.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public final class AuthDto {
  private AuthDto() {}

  public record RegisterRequest(
      @NotBlank @Size(max = 80) String firstName,
      @NotBlank @Size(max = 80) String lastName,
      @NotBlank @Email @Size(max = 254) String email,
      @NotBlank @Size(min = 8, max = 72) String password,
      @Size(max = 2000) String description) {
    @Override
    public String toString() {
      return "RegisterRequest[firstName="
          + firstName
          + ", lastName="
          + lastName
          + ", email="
          + email
          + ", password=[PROTECTED], description="
          + description
          + "]";
    }
  }

  public record LoginRequest(@NotBlank @Email String email, @NotBlank String password) {
    @Override
    public String toString() {
      return "LoginRequest[email=" + email + ", password=[PROTECTED]]";
    }
  }

  public record AuthResponse(
      String accessToken,
      String refreshToken,
      String tokenType,
      Instant expiresAt,
      Instant refreshExpiresAt,
      UserResponse user) {}

  public record RefreshRequest(@NotBlank String refreshToken) {}

  public record UserResponse(
      Long id,
      String firstName,
      String lastName,
      String email,
      String description,
      String country,
      Instant createdAt) {
    public static UserResponse from(User user) {
      return new UserResponse(
          user.getId(),
          user.getFirstName(),
          user.getLastName(),
          user.getEmail(),
          user.getDescription(),
          user.getCountry(),
          user.getCreatedAt());
    }
  }

  public record ForgotPasswordRequest(@NotBlank @Email String email) {}

  public record ResetPasswordRequest(
      @NotBlank String token, @NotBlank @Size(min = 8, max = 72) String password) {}
}
