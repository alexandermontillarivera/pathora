package io.pathora.catalog.modules.user;

import io.pathora.catalog.entities.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public final class UserDto {
  private UserDto() {}

  public record UpdateRequest(
      @NotBlank @Size(max = 80) String firstName,
      @NotBlank @Size(max = 80) String lastName,
      @Size(max = 2000) String description,
      @Pattern(regexp = "^[A-Za-z]{2}$") String country,
      @Pattern(regexp = "^[A-Za-z0-9_-]{1,80}$") String avatarSeed) {}

  public record Response(
      Long id,
      String firstName,
      String lastName,
      String description,
      String country,
      String avatarSeed,
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
          user.getAvatarSeed(),
          user.getCreatedAt(),
          comments,
          ratings);
    }
  }
}
