package io.pathora.catalog.repositories;

import io.pathora.catalog.entities.School;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SchoolRepository extends JpaRepository<School, Long> {
  List<School> findAllByOrderByNameAsc();
}
