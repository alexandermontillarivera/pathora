package io.pathora.catalog.modules.auth;

import io.pathora.catalog.entities.PasswordResetToken;
import io.pathora.catalog.entities.RefreshToken;
import io.pathora.catalog.entities.User;
import io.pathora.catalog.repositories.AccountNotificationRepository;
import io.pathora.catalog.repositories.PasswordResetTokenRepository;
import io.pathora.catalog.repositories.RefreshTokenRepository;
import io.pathora.catalog.repositories.UserRepository;
import io.pathora.catalog.shared.email.EmailService;
import io.pathora.catalog.shared.exception.ConflictException;
import io.pathora.catalog.shared.exception.ResourceNotFoundException;
import io.pathora.catalog.shared.exception.UnauthorizedException;
import io.pathora.catalog.shared.security.JwtService;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuthService {
  private final UserRepository users;
  private final PasswordEncoder passwords;
  private final JwtService jwt;
  private final EmailService email;
  private final PasswordResetTokenRepository resetTokens;
  private final AccountNotificationRepository notifications;
  private final RefreshTokenRepository refreshTokens;
  private final Duration refreshExpiration;
  private final SecureRandom random = new SecureRandom();

  public AuthService(
      UserRepository users,
      PasswordEncoder passwords,
      JwtService jwt,
      EmailService email,
      PasswordResetTokenRepository resetTokens,
      AccountNotificationRepository notifications,
      RefreshTokenRepository refreshTokens,
      @Value("${app.jwt.refresh-expiration-days}") long refreshExpirationDays) {
    this.users = users;
    this.passwords = passwords;
    this.jwt = jwt;
    this.email = email;
    this.resetTokens = resetTokens;
    this.notifications = notifications;
    this.refreshTokens = refreshTokens;
    this.refreshExpiration = Duration.ofDays(refreshExpirationDays);
  }

  @Transactional
  public AuthDto.AuthResponse register(AuthDto.RegisterRequest request) {
    var normalizedEmail = request.email().trim().toLowerCase();

    if (users.existsByEmailIgnoreCase(normalizedEmail))
      throw new ConflictException("An account with this email already exists.");
    var user =
        users.save(
            new User(
                request.firstName().trim(),
                request.lastName().trim(),
                normalizedEmail,
                passwords.encode(request.password()),
                request.description() == null ? "" : request.description().trim()));
    email.sendWelcome(user);
    return response(user);
  }

  @Transactional
  public AuthDto.AuthResponse login(AuthDto.LoginRequest request) {
    var user =
        users
            .findByEmailIgnoreCase(request.email().trim())
            .orElseThrow(() -> new UnauthorizedException("Invalid email or password."));
    if (!passwords.matches(request.password(), user.getPasswordHash()))
      throw new UnauthorizedException("Invalid email or password.");
    notifications.save(
        new io.pathora.catalog.entities.AccountNotification(
            user,
            "LOGIN",
            "Nuevo inicio de sesión",
            "Se accedió a tu cuenta desde una nueva sesión.",
            "/activity"));
    return response(user);
  }

  public AuthDto.UserResponse me(Long userId) {
    return AuthDto.UserResponse.from(getUser(userId));
  }

  @Transactional
  public AuthDto.AuthResponse refresh(AuthDto.RefreshRequest request) {
    var stored =
        refreshTokens
            .findByTokenHash(hash(request.refreshToken()))
            .filter(RefreshToken::isValid)
            .orElseThrow(() -> new UnauthorizedException("Refresh token is invalid or expired."));
    stored.revoke();
    return response(stored.getUser());
  }

  @Transactional
  public void logout(AuthDto.RefreshRequest request) {
    refreshTokens.findByTokenHash(hash(request.refreshToken())).ifPresent(RefreshToken::revoke);
  }

  public User getUser(Long id) {
    return users.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found."));
  }

  @Transactional
  public void forgotPassword(AuthDto.ForgotPasswordRequest request) {
    users
        .findByEmailIgnoreCase(request.email().trim())
        .ifPresent(
            user -> {
              resetTokens.deleteAllByUserId(user.getId());
              var bytes = new byte[32];
              random.nextBytes(bytes);
              var rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
              resetTokens.save(
                  new PasswordResetToken(
                      user, hash(rawToken), Instant.now().plus(Duration.ofMinutes(30))));
              email.sendPasswordReset(user, rawToken);
            });
  }

  @Transactional
  public void resetPassword(AuthDto.ResetPasswordRequest request) {
    var token =
        resetTokens
            .findByTokenHash(hash(request.token()))
            .filter(PasswordResetToken::isValid)
            .orElseThrow(
                () -> new UnauthorizedException("Password reset link is invalid or expired."));
    token.getUser().changePassword(passwords.encode(request.password()));
    token.consume();
    refreshTokens.deleteAllByUserId(token.getUser().getId());
  }

  private String hash(String value) {
    try {
      return HexFormat.of()
          .formatHex(
              MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)));
    } catch (java.security.NoSuchAlgorithmException ex) {
      throw new IllegalStateException(ex);
    }
  }

  private AuthDto.AuthResponse response(User user) {
    var token = jwt.issue(user);
    var rawRefreshToken = randomToken();
    var refreshExpiresAt = Instant.now().plus(refreshExpiration);
    refreshTokens.save(new RefreshToken(user, hash(rawRefreshToken), refreshExpiresAt));
    return new AuthDto.AuthResponse(
        token.value(),
        rawRefreshToken,
        "Bearer",
        token.expiresAt(),
        refreshExpiresAt,
        AuthDto.UserResponse.from(user));
  }

  private String randomToken() {
    var bytes = new byte[48];
    random.nextBytes(bytes);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
  }
}
