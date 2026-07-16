package io.pathora.catalog.modules.notification;

import io.pathora.catalog.repositories.AccountNotificationRepository;
import io.pathora.catalog.shared.exception.ResourceNotFoundException;
import io.pathora.catalog.shared.pagination.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class NotificationService {
  private final AccountNotificationRepository repository;

  public NotificationService(AccountNotificationRepository repository) {
    this.repository = repository;
  }

  public PageResponse<NotificationDto.Response> list(Long userId, PaginationRequest pagination) {
    return PageResponse.from(
        repository
            .findAllByUserId(userId, pagination.pageable("createdAt"))
            .map(NotificationDto.Response::from),
        pagination);
  }

  public NotificationDto.Summary summary(Long userId) {
    return new NotificationDto.Summary(repository.countByUserIdAndReadFalse(userId));
  }

  @Transactional
  public void read(Long userId, Long id) {
    var item =
        repository
            .findById(id)
            .filter(value -> value.getUser().getId().equals(userId))
            .orElseThrow(() -> new ResourceNotFoundException("No se encontró la notificación."));
    item.markRead();
  }

  @Transactional
  public void readAll(Long userId) {
    repository.findAllByUserIdAndReadFalse(userId).forEach(value -> value.markRead());
  }
}
