package io.pathora.catalog.modules.career;

import io.pathora.catalog.entities.Career;
import io.pathora.catalog.enums.CareerStatus;
import io.pathora.catalog.enums.StudyMode;
import java.time.Instant;

public final class CareerDto {
  private CareerDto() {}

  public record Response(
      Long id,
      String code,
      String name,
      String imageUrl,
      String description,
      String heroSummary,
      String overview,
      String professionalProfile,
      java.util.List<String> outcomes,
      SchoolSummary school,
      Short durationTerms,
      double durationYears,
      StudyMode studyMode,
      CareerStatus status,
      double rating,
      long reviews,
      Instant createdAt,
      Instant updatedAt) {
    static Response from(Career c, double rating, long reviews) {
      return new Response(
          c.getId(),
          c.getCode(),
          c.getName(),
          c.getImageUrl(),
          c.getDescription(),
          c.getHeroSummary(),
          c.getOverview(),
          c.getProfessionalProfile(),
          c.getOutcomes(),
          new SchoolSummary(c.getSchool().getId(), c.getSchool().getName()),
          c.getDurationTerms(),
          c.getDurationTerms() / 3.0,
          c.getStudyMode(),
          c.getStatus(),
          Math.round(rating * 100.0) / 100.0,
          reviews,
          c.getCreatedAt(),
          c.getUpdatedAt());
    }
  }

  public record SchoolSummary(Long id, String name) {}
}
