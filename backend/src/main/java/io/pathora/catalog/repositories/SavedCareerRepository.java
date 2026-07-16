package io.pathora.catalog.repositories;

import io.pathora.catalog.entities.SavedCareer;
import java.util.Optional;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;

public interface SavedCareerRepository extends JpaRepository<SavedCareer, Long> {
  @Modifying(flushAutomatically = true)
  @Query("delete from SavedCareer saved where saved.user.id = :userId")
  void deleteAllByUserId(Long userId);

  @EntityGraph(attributePaths = {"career", "career.school"})
  Page<SavedCareer> findAllByUserId(Long userId, Pageable pageable);

  Optional<SavedCareer> findByUserIdAndCareerId(Long userId, Long careerId);
}
