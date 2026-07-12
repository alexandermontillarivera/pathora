package io.pathora.catalog.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pensums")
public class Pensum {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "career_id", unique = true)
  private Career career;

  @Column(nullable = false, length = 20)
  private String version;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String description;

  @Column(name = "effective_from", nullable = false)
  private Short effectiveFrom;

  @Column(nullable = false)
  private boolean active;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @OneToMany(mappedBy = "pensum", fetch = FetchType.LAZY)
  @OrderBy("termNumber ASC, position ASC")
  private List<PensumSubject> subjects = new ArrayList<>();

  protected Pensum() {}

  @PrePersist
  void onCreate() {
    createdAt = Instant.now();
  }

  public Long getId() {
    return id;
  }

  public Career getCareer() {
    return career;
  }

  public String getVersion() {
    return version;
  }

  public String getDescription() {
    return description;
  }

  public Short getEffectiveFrom() {
    return effectiveFrom;
  }

  public boolean isActive() {
    return active;
  }

  public List<PensumSubject> getSubjects() {
    return subjects;
  }
}
