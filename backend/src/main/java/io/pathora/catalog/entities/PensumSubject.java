package io.pathora.catalog.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "pensum_subjects")
public class PensumSubject {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "pensum_id")
  private Pensum pensum;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "subject_id")
  private Subject subject;

  @Column(name = "term_number", nullable = false)
  private Short termNumber;

  @Column(nullable = false)
  private Short position;

  @Column(nullable = false)
  private boolean mandatory;

  protected PensumSubject() {}

  public Long getId() {
    return id;
  }

  public Subject getSubject() {
    return subject;
  }

  public Short getTermNumber() {
    return termNumber;
  }

  public Short getPosition() {
    return position;
  }

  public boolean isMandatory() {
    return mandatory;
  }
}
