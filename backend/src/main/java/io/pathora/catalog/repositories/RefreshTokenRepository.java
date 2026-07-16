package io.pathora.catalog.repositories;

import io.pathora.catalog.entities.RefreshToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
  Optional<RefreshToken> findByTokenHash(String tokenHash);

  @org.springframework.data.jpa.repository.Modifying(flushAutomatically = true)
  @org.springframework.data.jpa.repository.Query(
      "delete from RefreshToken token where token.user.id = :userId")
  void deleteAllByUserId(Long userId);
}
