package io.pathora.catalog.entities;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "schools")
public class School {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 120)
  private String name;

  @Column(name = "image_url", nullable = false, length = 600)
  private String imageUrl;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String description;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  protected School() {}

  public School(String name, String imageUrl, String description) {
    update(name, imageUrl, description);
  }

  @PrePersist
  void onCreate() {
    createdAt = Instant.now();
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public String getDescription() {
    return description;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void update(String name, String imageUrl, String description) {
    this.name = name;
    this.imageUrl = imageUrl;
    this.description = description;
  }
}
