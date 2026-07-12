package io.pathora.catalog.modules.rating;

import io.pathora.catalog.entities.Rating;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Map;

public final class RatingDto {
  private RatingDto() {}

  public record Request(@NotNull @Min(1) @Max(5) Short value) {}

  public record Response(
      Long id,
      Short value,
      UserSummary user,
      CareerSummary career,
      Instant createdAt,
      Instant updatedAt) {
    static Response from(Rating rating) {
      var user = rating.getUser();
      return new Response(
          rating.getId(),
          rating.getValue(),
          new UserSummary(user.getId(), user.getFirstName(), user.getLastName()),
          new CareerSummary(rating.getCareer().getId(), rating.getCareer().getName()),
          rating.getCreatedAt(),
          rating.getUpdatedAt());
    }
  }

  public record UserSummary(Long id, String firstName, String lastName) {}

  public record CareerSummary(Long id, String name) {}

  public record Summary(long count, double average, Map<Integer, Long> distribution) {}
}
