package io.pathora.catalog.modules.school;

import io.pathora.catalog.entities.School;

public final class SchoolDto {
  private SchoolDto() {}

  public record Response(Long id, String name, String imageUrl, String description, long programs) {
    static Response from(School school, long programs) {
      return new Response(
          school.getId(),
          school.getName(),
          school.getImageUrl(),
          school.getDescription(),
          programs);
    }
  }
}
