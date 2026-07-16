package io.pathora.catalog.repositories;

import io.pathora.catalog.entities.CommentVote;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentVoteRepository extends JpaRepository<CommentVote, Long> {
  Optional<CommentVote> findByCommentIdAndUserId(Long commentId, Long userId);

  long countByCommentIdAndUseful(Long commentId, boolean useful);

  void deleteAllByCommentId(Long commentId);

  @org.springframework.data.jpa.repository.Modifying(flushAutomatically = true)
  @org.springframework.data.jpa.repository.Query(
      "delete from CommentVote vote where vote.comment.user.id = :userId")
  void deleteAllByCommentUserId(Long userId);

  @org.springframework.data.jpa.repository.Modifying(flushAutomatically = true)
  @org.springframework.data.jpa.repository.Query(
      "delete from CommentVote vote where vote.user.id = :userId")
  void deleteAllByUserId(Long userId);
}
