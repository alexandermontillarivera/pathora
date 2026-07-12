package io.pathora.catalog.modules.notification;

import io.pathora.catalog.entities.AccountNotification;
import java.time.Instant;

public final class NotificationDto {
  private NotificationDto() {}

  public record Response(
      Long id,
      String type,
      String title,
      String detail,
      String targetUrl,
      boolean read,
      Instant createdAt) {
    static Response from(AccountNotification n) {
      return new Response(
          n.getId(),
          n.getType(),
          n.getTitle(),
          n.getDetail(),
          n.getTargetUrl(),
          n.isRead(),
          n.getCreatedAt());
    }
  }

  public record Summary(long unread) {}
}
