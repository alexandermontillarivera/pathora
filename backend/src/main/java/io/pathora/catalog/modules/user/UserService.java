package io.pathora.catalog.modules.user;

import io.pathora.catalog.repositories.CareerCommentRepository;
import io.pathora.catalog.repositories.RatingRepository;
import io.pathora.catalog.repositories.UserRepository;
import io.pathora.catalog.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserService {
  private final UserRepository users;
  private final CareerCommentRepository comments;
  private final RatingRepository ratings;

  public UserService(
      UserRepository users, CareerCommentRepository comments, RatingRepository ratings) {
    this.users = users;
    this.comments = comments;
    this.ratings = ratings;
  }

  public UserDto.Response findById(Long id) {
    var user =
        users.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found."));
    return response(user);
  }

  @Transactional
  public UserDto.Response update(Long id, UserDto.UpdateRequest request) {
    var user =
        users.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found."));
    user.updateProfile(
        request.firstName().trim(),
        request.lastName().trim(),
        request.description() == null ? "" : request.description().trim(),
        request.country() == null ? null : request.country().trim());
    return response(user);
  }

  private UserDto.Response response(io.pathora.catalog.entities.User user) {
    return UserDto.Response.from(
        user, comments.countByUserId(user.getId()), ratings.countByUserId(user.getId()));
  }
}
