package io.pathora.catalog.repositories;

import io.pathora.catalog.entities.Career;
import org.springframework.data.jpa.domain.Specification;

public final class CareerSpecifications {
  private CareerSpecifications() {}

  public static Specification<Career> nameContains(String name) {
    return (root, query, cb) ->
        name == null || name.isBlank()
            ? cb.conjunction()
            : cb.like(cb.lower(root.get("name")), "%" + name.trim().toLowerCase() + "%");
  }

  public static Specification<Career> schoolIs(Long schoolId) {
    return (root, query, cb) ->
        schoolId == null ? cb.conjunction() : cb.equal(root.get("school").get("id"), schoolId);
  }

  public static Specification<Career> studyModeIs(io.pathora.catalog.enums.StudyMode studyMode) {
    return (root, query, cb) ->
        studyMode == null ? cb.conjunction() : cb.equal(root.get("studyMode"), studyMode);
  }

  public static Specification<Career> statusIs(io.pathora.catalog.enums.CareerStatus status) {
    return (root, query, cb) ->
        status == null ? cb.conjunction() : cb.equal(root.get("status"), status);
  }
}
