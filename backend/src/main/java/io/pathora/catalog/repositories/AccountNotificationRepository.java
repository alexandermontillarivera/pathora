package io.pathora.catalog.repositories;

import io.pathora.catalog.entities.AccountNotification;
import java.util.List;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;

public interface AccountNotificationRepository extends JpaRepository<AccountNotification, Long> {
  void deleteAllByUserId(Long userId);

  Page<AccountNotification> findAllByUserId(Long userId, Pageable pageable);

  long countByUserIdAndReadFalse(Long userId);

  List<AccountNotification> findAllByUserIdAndReadFalse(Long userId);
}
