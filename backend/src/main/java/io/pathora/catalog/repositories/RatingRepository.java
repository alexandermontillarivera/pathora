package io.pathora.catalog.repositories;

import io.pathora.catalog.entities.Rating;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RatingRepository extends JpaRepository<Rating, Long> {
  boolean existsByUserIdAndCareerId(Long userId, Long careerId);

  java.util.Optional<Rating> findByUserIdAndCareerId(Long userId, Long careerId);

  List<Rating> findAllByCareerId(Long careerId);

  long countByUserId(Long userId);

  long countByCareerId(Long careerId);

  @Query("select coalesce(avg(r.value), 0) from Rating r where r.career.id = :careerId")
  double averageByCareerId(Long careerId);

  @org.springframework.data.jpa.repository.EntityGraph(
      attributePaths = {"user", "career", "career.school"})
  org.springframework.data.domain.Page<Rating> findAllByUserId(
      Long userId, org.springframework.data.domain.Pageable pageable);

  @Query("select coalesce(avg(r.value), 0) from Rating r")
  double globalAverage();
}
