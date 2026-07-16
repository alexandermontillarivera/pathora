package io.pathora.catalog.repositories;

import io.pathora.catalog.entities.PasswordResetToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
  Optional<PasswordResetToken> findByTokenHash(String tokenHash);

  @org.springframework.data.jpa.repository.Modifying(flushAutomatically = true)
  @org.springframework.data.jpa.repository.Query(
      "delete from PasswordResetToken token where token.user.id = :userId")
  void deleteAllByUserId(Long userId);
}
