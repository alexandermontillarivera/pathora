package io.pathora.catalog.entities;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
    name = "comment_votes",
    uniqueConstraints = @UniqueConstraint(columnNames = {"comment_id", "user_id"}))
public class CommentVote {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "comment_id")
  private CareerComment comment;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id")
  private User user;

  @Column(nullable = false)
  private boolean useful;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  protected CommentVote() {}

  public CommentVote(CareerComment comment, User user, boolean useful) {
    this.comment = comment;
    this.user = user;
    this.useful = useful;
  }

  @PrePersist
  void onCreate() {
    createdAt = Instant.now();
  }

  public void update(boolean useful) {
    this.useful = useful;
  }

  public boolean isUseful() {
    return useful;
  }
}
