package io.pathora.catalog.repositories;

import io.pathora.catalog.entities.Career;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CareerRepository
    extends JpaRepository<Career, Long>, JpaSpecificationExecutor<Career> {
  long countBySchoolId(Long schoolId);
}
