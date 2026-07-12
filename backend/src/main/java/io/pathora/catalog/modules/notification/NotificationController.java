package io.pathora.catalog.modules.notification;

import io.pathora.catalog.shared.api.ApiResponse;
import io.pathora.catalog.shared.pagination.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/me/notifications")
@SecurityRequirement(name = "bearerAuth")
public class NotificationController {
  private final NotificationService service;

  public NotificationController(NotificationService service) {
    this.service = service;
  }

  @GetMapping
  ApiResponse<PageResponse<NotificationDto.Response>> list(
      @AuthenticationPrincipal Jwt jwt, @Valid @ModelAttribute PaginationRequest pagination) {
    return ApiResponse.ok("Notifications retrieved.", service.list(id(jwt), pagination));
  }

  @GetMapping("/summary")
  ApiResponse<NotificationDto.Summary> summary(@AuthenticationPrincipal Jwt jwt) {
    return ApiResponse.ok("Notification summary retrieved.", service.summary(id(jwt)));
  }

  @PutMapping("/{notificationId}/read")
  ApiResponse<Void> read(@AuthenticationPrincipal Jwt jwt, @PathVariable Long notificationId) {
    service.read(id(jwt), notificationId);
    return ApiResponse.ok("Notification read.", null);
  }

  @PutMapping("/read-all")
  ApiResponse<Void> readAll(@AuthenticationPrincipal Jwt jwt) {
    service.readAll(id(jwt));
    return ApiResponse.ok("Notifications read.", null);
  }

  private Long id(Jwt jwt) {
    return Long.valueOf(jwt.getSubject());
  }
}
