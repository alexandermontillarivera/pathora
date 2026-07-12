package io.pathora.catalog.modules.comment;

import io.pathora.catalog.entities.CareerComment;
import io.pathora.catalog.repositories.*;
import io.pathora.catalog.shared.exception.*;
import io.pathora.catalog.shared.pagination.PageResponse;
import io.pathora.catalog.shared.pagination.PaginationRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;

@Service
@Transactional(readOnly = true)
public class CommentService {
  private static final int MAX_CONTENT_LENGTH = 50_000;
  private final CareerCommentRepository comments;
  private final UserRepository users;
  private final CareerRepository careers;
  private final CommentVoteRepository votes;
  private final AccountNotificationRepository notifications;

  public CommentService(
      CareerCommentRepository comments,
      UserRepository users,
      CareerRepository careers,
      CommentVoteRepository votes,
      AccountNotificationRepository notifications) {
    this.comments = comments;
    this.users = users;
    this.careers = careers;
    this.votes = votes;
    this.notifications = notifications;
  }

  public PageResponse<CommentDto.Response> findAll(Long careerId, PaginationRequest pagination) {
    ensureCareer(careerId);
    var page =
        comments
            .findAllByCareerIdAndParentIsNull(careerId, pagination.pageable("createdAt"))
            .map(this::response);
    return PageResponse.from(page, pagination);
  }

  @Transactional
  public CommentDto.Response create(Long careerId, Long userId, CommentDto.Request request) {
    validate(request.content());
    var comment =
        new CareerComment(user(userId), ensureCareer(careerId), request.content().toString());
    return response(comments.save(comment));
  }

  public PageResponse<CommentDto.Response> findRecent(PaginationRequest pagination) {
    var page = comments.findAllByParentIsNull(pagination.pageable("createdAt")).map(this::response);
    return PageResponse.from(page, pagination);
  }

  public PageResponse<CommentDto.Response> findByUser(Long userId, PaginationRequest pagination) {
    user(userId);
    var page =
        comments
            .findAllByUserIdAndParentIsNull(userId, pagination.pageable("createdAt"))
            .map(this::response);
    return PageResponse.from(page, pagination);
  }

  @Transactional
  public CommentDto.Response reply(Long parentId, Long userId, CommentDto.Request request) {
    validate(request.content());
    var parent =
        comments
            .findById(parentId)
            .orElseThrow(() -> new ResourceNotFoundException("Comment not found."));
    var root = parent.getParent() == null ? parent : parent.getParent();
    var author = user(userId);
    var reply =
        comments.save(
            new CareerComment(author, root.getCareer(), root, request.content().toString()));
    if (!root.getUser().getId().equals(userId))
      notifications.save(
          new io.pathora.catalog.entities.AccountNotification(
              root.getUser(),
              "REPLY",
              author.getFirstName() + " respondió a tu comentario",
              "Hay una nueva respuesta en " + root.getCareer().getName(),
              "/careers/" + root.getCareer().getId()));
    return response(reply);
  }

  @Transactional
  public CommentDto.VoteResponse vote(Long commentId, Long userId, CommentDto.VoteRequest request) {
    var comment =
        comments
            .findById(commentId)
            .orElseThrow(() -> new ResourceNotFoundException("Comment not found."));
    var vote =
        votes
            .findByCommentIdAndUserId(commentId, userId)
            .orElseGet(
                () ->
                    new io.pathora.catalog.entities.CommentVote(
                        comment, user(userId), request.useful()));
    vote.update(request.useful());
    votes.save(vote);
    return voteResponse(commentId, request.useful());
  }

  @Transactional
  public CommentDto.VoteResponse removeVote(Long commentId, Long userId) {
    votes.findByCommentIdAndUserId(commentId, userId).ifPresent(votes::delete);
    return voteResponse(commentId, null);
  }

  @Transactional
  public CommentDto.Response update(Long id, Long userId, CommentDto.Request request) {
    validate(request.content());
    var comment = owned(id, userId);
    comment.update(request.content().toString());
    return response(comment);
  }

  @Transactional
  public void delete(Long id, Long userId) {
    var comment = owned(id, userId);
    comments
        .findAllByParentId(id)
        .forEach(
            reply -> {
              votes.deleteAllByCommentId(reply.getId());
              comments.delete(reply);
            });
    votes.deleteAllByCommentId(id);
    comments.delete(comment);
  }

  private void validate(JsonNode content) {
    if ((!content.isObject() && !content.isArray()) || content.isEmpty())
      throw new IllegalArgumentException(
          "Rich-text content must be a non-empty JSON object or array.");
    if (content.toString().length() > MAX_CONTENT_LENGTH)
      throw new IllegalArgumentException("Comment content is too large.");
  }

  private CareerComment owned(Long id, Long userId) {
    var comment =
        comments
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Comment not found."));
    if (!comment.getUser().getId().equals(userId))
      throw new ForbiddenException("You can only modify your own comment.");
    return comment;
  }

  private io.pathora.catalog.entities.User user(Long id) {
    return users.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found."));
  }

  private CommentDto.Response response(CareerComment comment) {
    var replies =
        comment.getParent() == null
            ? comments.findAllByParentIdOrderByCreatedAtAsc(comment.getId()).stream()
                .map(this::response)
                .toList()
            : java.util.List.<CommentDto.Response>of();
    return CommentDto.Response.from(
        comment,
        votes.countByCommentIdAndUseful(comment.getId(), true),
        votes.countByCommentIdAndUseful(comment.getId(), false),
        replies);
  }

  private CommentDto.VoteResponse voteResponse(Long commentId, Boolean currentVote) {
    return new CommentDto.VoteResponse(
        votes.countByCommentIdAndUseful(commentId, true),
        votes.countByCommentIdAndUseful(commentId, false),
        currentVote);
  }

  private io.pathora.catalog.entities.Career ensureCareer(Long id) {
    return careers
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Career not found."));
  }
}
