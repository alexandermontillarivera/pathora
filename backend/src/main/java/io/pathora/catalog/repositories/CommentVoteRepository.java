package io.pathora.catalog.repositories;

import io.pathora.catalog.entities.CommentVote;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentVoteRepository extends JpaRepository<CommentVote, Long> {
  Optional<CommentVote> findByCommentIdAndUserId(Long commentId, Long userId);

  long countByCommentIdAndUseful(Long commentId, boolean useful);

  void deleteAllByCommentId(Long commentId);

  void deleteAllByCommentUserId(Long userId);

  void deleteAllByUserId(Long userId);
}
