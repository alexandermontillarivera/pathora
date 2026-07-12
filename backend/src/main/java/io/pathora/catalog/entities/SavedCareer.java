package io.pathora.catalog.entities;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
    name = "saved_careers",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "career_id"}))
public class SavedCareer {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "career_id")
  private Career career;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  protected SavedCareer() {}

  public SavedCareer(User user, Career career) {
    this.user = user;
    this.career = career;
  }

  @PrePersist
  void create() {
    createdAt = Instant.now();
  }

  public Long getId() {
    return id;
  }

  public Career getCareer() {
    return career;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}
