package io.pathora.catalog.entities;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "app_users")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "first_name", nullable = false, length = 80)
  private String firstName;

  @Column(name = "last_name", nullable = false, length = 80)
  private String lastName;

  @Column(nullable = false, unique = true, length = 254)
  private String email;

  @Column(name = "password_hash", nullable = false, length = 100)
  private String passwordHash;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String description;

  @Column(length = 80)
  private String country;

  @Column(name = "avatar_seed", length = 80)
  private String avatarSeed;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  protected User() {}

  public User(
      String firstName,
      String lastName,
      String email,
      String passwordHash,
      String description,
      String country) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.passwordHash = passwordHash;
    this.description = description;
    this.country = country;
  }

  @PrePersist
  void onCreate() {
    createdAt = updatedAt = Instant.now();
  }

  @PreUpdate
  void onUpdate() {
    updatedAt = Instant.now();
  }

  public Long getId() {
    return id;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getEmail() {
    return email;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public String getDescription() {
    return description;
  }

  public String getCountry() {
    return country;
  }

  public String getAvatarSeed() {
    return avatarSeed;
  }

  public void updateProfile(
      String firstName, String lastName, String description, String country, String avatarSeed) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.description = description;
    this.country = country;
    this.avatarSeed = avatarSeed;
  }

  public void changePassword(String passwordHash) {
    this.passwordHash = passwordHash;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}
