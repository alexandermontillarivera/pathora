package io.pathora.catalog.modules.user;

import io.pathora.catalog.repositories.*;
import io.pathora.catalog.shared.exception.ResourceNotFoundException;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserService {
  private final UserRepository users;
  private final CareerCommentRepository comments;
  private final RatingRepository ratings;
  private final CommentVoteRepository votes;
  private final SavedCareerRepository savedCareers;
  private final AccountNotificationRepository notifications;
  private final RefreshTokenRepository refreshTokens;
  private final PasswordResetTokenRepository passwordResetTokens;

  public UserService(
      UserRepository users,
      CareerCommentRepository comments,
      RatingRepository ratings,
      CommentVoteRepository votes,
      SavedCareerRepository savedCareers,
      AccountNotificationRepository notifications,
      RefreshTokenRepository refreshTokens,
      PasswordResetTokenRepository passwordResetTokens) {
    this.users = users;
    this.comments = comments;
    this.ratings = ratings;
    this.votes = votes;
    this.savedCareers = savedCareers;
    this.notifications = notifications;
    this.refreshTokens = refreshTokens;
    this.passwordResetTokens = passwordResetTokens;
  }

  public UserDto.Response findById(Long id) {
    var user =
        users
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("No se encontró el usuario."));
    return response(user);
  }

  @Transactional
  public UserDto.Response update(Long id, UserDto.UpdateRequest request) {
    var user =
        users
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("No se encontró el usuario."));
    user.updateProfile(
        request.firstName().trim(),
        request.lastName().trim(),
        request.description() == null ? "" : request.description().trim(),
        request.country() == null ? null : request.country().trim().toUpperCase(Locale.ROOT),
        request.avatarSeed() == null ? null : request.avatarSeed().trim());
    return response(user);
  }

  @Transactional
  public void delete(Long id) {
    var user =
        users
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("No se encontró el usuario."));

    votes.deleteAllByCommentUserId(id);
    votes.deleteAllByUserId(id);
    comments.detachRepliesFromCommentsByUserId(id);
    comments.deleteAllByUserId(id);
    ratings.deleteAllByUserId(id);
    savedCareers.deleteAllByUserId(id);
    notifications.deleteAllByUserId(id);
    passwordResetTokens.deleteAllByUserId(id);
    refreshTokens.deleteAllByUserId(id);
    users.delete(user);
  }

  private UserDto.Response response(io.pathora.catalog.entities.User user) {
    return UserDto.Response.from(
        user, comments.countByUserId(user.getId()), ratings.countByUserId(user.getId()));
  }
}
