package io.pathora.catalog.repositories;

import io.pathora.catalog.entities.Pensum;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PensumRepository extends JpaRepository<Pensum, Long> {
  @EntityGraph(attributePaths = {"career", "subjects", "subjects.subject"})
  Optional<Pensum> findByCareerIdAndActiveTrue(Long careerId);
}
