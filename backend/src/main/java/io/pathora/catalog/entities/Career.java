package io.pathora.catalog.entities;

import io.pathora.catalog.enums.CareerStatus;
import io.pathora.catalog.enums.StudyMode;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "careers")
public class Career {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 20)
  private String code;

  @Column(nullable = false, length = 160)
  private String name;

  @Column(name = "image_url", nullable = false, length = 600)
  private String imageUrl;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String description;

  @Column(name = "hero_summary", columnDefinition = "TEXT")
  private String heroSummary;

  @Column(columnDefinition = "TEXT")
  private String overview;

  @Column(name = "professional_profile", columnDefinition = "TEXT")
  private String professionalProfile;

  @ElementCollection(fetch = FetchType.LAZY)
  @CollectionTable(name = "career_outcomes", joinColumns = @JoinColumn(name = "career_id"))
  @Column(name = "outcome", nullable = false, columnDefinition = "TEXT")
  @OrderColumn(name = "position")
  private List<String> outcomes = new ArrayList<>();

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "school_id")
  private School school;

  @Column(name = "duration_terms", nullable = false)
  private Short durationTerms;

  @Enumerated(EnumType.STRING)
  @Column(name = "study_mode", nullable = false, length = 12)
  private StudyMode studyMode;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 8)
  private CareerStatus status;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  protected Career() {}

  public Career(
      String code,
      String name,
      String imageUrl,
      String description,
      School school,
      Short durationTerms,
      StudyMode studyMode,
      CareerStatus status) {
    update(code, name, imageUrl, description, school, durationTerms, studyMode, status);
  }

  public void update(
      String code,
      String name,
      String imageUrl,
      String description,
      School school,
      Short durationTerms,
      StudyMode studyMode,
      CareerStatus status) {
    this.code = code;
    this.name = name;
    this.school = school;
    this.imageUrl = imageUrl;
    this.description = description;
    this.durationTerms = durationTerms;
    this.studyMode = studyMode;
    this.status = status;
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

  public String getCode() {
    return code;
  }

  public String getName() {
    return name;
  }

  public School getSchool() {
    return school;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public String getDescription() {
    return description;
  }

  public String getHeroSummary() {
    return heroSummary;
  }

  public String getOverview() {
    return overview;
  }

  public String getProfessionalProfile() {
    return professionalProfile;
  }

  public List<String> getOutcomes() {
    return List.copyOf(outcomes);
  }

  public Short getDurationTerms() {
    return durationTerms;
  }

  public StudyMode getStudyMode() {
    return studyMode;
  }

  public CareerStatus getStatus() {
    return status;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }
}
