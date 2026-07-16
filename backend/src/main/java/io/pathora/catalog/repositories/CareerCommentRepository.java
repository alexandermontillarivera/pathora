package io.pathora.catalog.repositories;

import io.pathora.catalog.entities.CareerComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CareerCommentRepository extends JpaRepository<CareerComment, Long> {
  @org.springframework.data.jpa.repository.Modifying(clearAutomatically = true)
  @org.springframework.data.jpa.repository.Query(
      "update CareerComment c set c.parent = null where c.parent.user.id = :userId")
  void detachRepliesFromCommentsByUserId(Long userId);

  void deleteAllByUserId(Long userId);

  @EntityGraph(attributePaths = "user")
  Page<CareerComment> findAllByCareerIdAndParentIsNull(Long careerId, Pageable pageable);

  @EntityGraph(attributePaths = {"user", "career"})
  Page<CareerComment> findAllByParentIsNull(Pageable pageable);

  @EntityGraph(attributePaths = "user")
  java.util.List<CareerComment> findAllByParentIdOrderByCreatedAtAsc(Long parentId);

  long countByUserId(Long userId);

  @EntityGraph(attributePaths = {"user", "career"})
  Page<CareerComment> findAllByUserIdAndParentIsNull(Long userId, Pageable pageable);

  java.util.List<CareerComment> findAllByParentId(Long parentId);

  long countByParentIsNull();

  @org.springframework.data.jpa.repository.Query(
      "select count(distinct c.career.id) from CareerComment c where c.parent is null")
  long countDistinctCareers();
}
