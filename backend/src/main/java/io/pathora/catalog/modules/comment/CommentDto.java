package io.pathora.catalog.modules.comment;

import io.pathora.catalog.entities.CareerComment;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

public final class CommentDto {
  private static final ObjectMapper JSON = new ObjectMapper();

  private CommentDto() {}

  public record Request(@NotNull JsonNode content) {}

  public record VoteRequest(@NotNull Boolean useful) {}

  public record Response(
      Long id,
      Long parentId,
      JsonNode content,
      Author author,
      CareerSummary career,
      long useful,
      long notUseful,
      java.util.List<Response> replies,
      Instant createdAt,
      Instant updatedAt) {
    static Response from(
        CareerComment comment, long useful, long notUseful, java.util.List<Response> replies) {
      var user = comment.getUser();
      var career = comment.getCareer();
      return new Response(
          comment.getId(),
          comment.getParent() == null ? null : comment.getParent().getId(),
          JSON.readTree(comment.getContent()),
          new Author(user.getId(), user.getFirstName(), user.getLastName()),
          new CareerSummary(career.getId(), career.getName()),
          useful,
          notUseful,
          replies,
          comment.getCreatedAt(),
          comment.getUpdatedAt());
    }
  }

  public record Author(Long id, String firstName, String lastName) {}

  public record CareerSummary(Long id, String name) {}

  public record VoteResponse(long useful, long notUseful, Boolean currentVote) {}
}
