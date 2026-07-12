package io.pathora.catalog.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "account_notifications")
public class AccountNotification {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id")
  private User user;

  @Column(nullable = false, length = 40)
  private String type;

  @Column(nullable = false, length = 180)
  private String title;

  @Column(nullable = false, length = 500)
  private String detail;

  @Column(name = "target_url", length = 300)
  private String targetUrl;

  @Column(nullable = false)
  private boolean read;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  protected AccountNotification() {}

  public AccountNotification(
      User user, String type, String title, String detail, String targetUrl) {
    this.user = user;
    this.type = type;
    this.title = title;
    this.detail = detail;
    this.targetUrl = targetUrl;
  }

  @PrePersist
  void create() {
    createdAt = Instant.now();
  }

  public Long getId() {
    return id;
  }

  public User getUser() {
    return user;
  }

  public String getType() {
    return type;
  }

  public String getTitle() {
    return title;
  }

  public String getDetail() {
    return detail;
  }

  public String getTargetUrl() {
    return targetUrl;
  }

  public boolean isRead() {
    return read;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void markRead() {
    read = true;
  }
}
