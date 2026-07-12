package io.pathora.catalog.repositories;

import io.pathora.catalog.entities.RefreshToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
  Optional<RefreshToken> findByTokenHash(String tokenHash);

  void deleteAllByUserId(Long userId);
}
