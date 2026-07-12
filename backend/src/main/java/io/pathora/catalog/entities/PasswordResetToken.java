package io.pathora.catalog.entities;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
    name = "password_reset_tokens",
    indexes = @Index(name = "idx_password_reset_hash", columnList = "token_hash", unique = true))
public class PasswordResetToken {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id")
  private User user;

  @Column(name = "token_hash", nullable = false, length = 64)
  private String tokenHash;

  @Column(name = "expires_at", nullable = false)
  private Instant expiresAt;

  @Column(name = "used_at")
  private Instant usedAt;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  protected PasswordResetToken() {}

  public PasswordResetToken(User user, String tokenHash, Instant expiresAt) {
    this.user = user;
    this.tokenHash = tokenHash;
    this.expiresAt = expiresAt;
  }

  @PrePersist
  void onCreate() {
    createdAt = Instant.now();
  }

  public User getUser() {
    return user;
  }

  public boolean isValid() {
    return usedAt == null && expiresAt.isAfter(Instant.now());
  }

  public void consume() {
    usedAt = Instant.now();
  }
}
