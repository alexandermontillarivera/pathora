package io.pathora.catalog.repositories;

import io.pathora.catalog.entities.PasswordResetToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
  Optional<PasswordResetToken> findByTokenHash(String tokenHash);

  void deleteAllByUserId(Long userId);
}
