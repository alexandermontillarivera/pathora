package io.pathora.catalog.modules.user;

import io.pathora.catalog.entities.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public final class UserDto {
  private UserDto() {}

  public record UpdateRequest(
      @NotBlank @Size(max = 80) String firstName,
      @NotBlank @Size(max = 80) String lastName,
      @Size(max = 2000) String description,
      @Size(max = 80) String country) {}

  public record Response(
      Long id,
      String firstName,
      String lastName,
      String description,
      String country,
      Instant createdAt,
      long comments,
      long ratings) {
    static Response from(User user, long comments, long ratings) {
      return new Response(
          user.getId(),
          user.getFirstName(),
          user.getLastName(),
          user.getDescription(),
          user.getCountry(),
          user.getCreatedAt(),
          comments,
          ratings);
    }
  }
}
