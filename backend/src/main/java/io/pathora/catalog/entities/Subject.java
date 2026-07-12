package io.pathora.catalog.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "subjects")
public class Subject {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 30)
  private String code;

  @Column(nullable = false, length = 200)
  private String name;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String description;

  @Column(nullable = false)
  private Short credits;

  @Column(name = "weekly_hours", nullable = false)
  private Short weeklyHours;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "subject_prerequisites",
      joinColumns = @JoinColumn(name = "subject_id"),
      inverseJoinColumns = @JoinColumn(name = "prerequisite_id"))
  @OrderBy("code ASC")
  private java.util.Set<Subject> prerequisites = new java.util.LinkedHashSet<>();

  protected Subject() {}

  public Long getId() {
    return id;
  }

  public String getCode() {
    return code;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public Short getCredits() {
    return credits;
  }

  public Short getWeeklyHours() {
    return weeklyHours;
  }

  public java.util.Set<Subject> getPrerequisites() {
    return java.util.Set.copyOf(prerequisites);
  }
}
