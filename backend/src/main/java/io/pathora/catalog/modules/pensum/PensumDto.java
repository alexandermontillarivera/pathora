package io.pathora.catalog.modules.pensum;

import io.pathora.catalog.entities.Pensum;
import io.pathora.catalog.entities.PensumSubject;
import java.util.List;

public final class PensumDto {
  private PensumDto() {}

  public record Response(
      Long id,
      String version,
      String description,
      Short effectiveFrom,
      boolean active,
      CareerSummary career,
      int totalCredits,
      List<Term> terms) {
    static Response from(Pensum pensum) {
      var terms =
          pensum.getSubjects().stream()
              .collect(
                  java.util.stream.Collectors.groupingBy(
                      PensumSubject::getTermNumber,
                      java.util.TreeMap::new,
                      java.util.stream.Collectors.toList()))
              .entrySet()
              .stream()
              .map(
                  entry ->
                      new Term(
                          entry.getKey(),
                          entry.getValue().stream().map(SubjectItem::from).toList()))
              .toList();
      int credits =
          terms.stream()
              .flatMap(term -> term.subjects().stream())
              .mapToInt(SubjectItem::credits)
              .sum();

      var career = pensum.getCareer();

      return new Response(
          pensum.getId(),
          pensum.getVersion(),
          pensum.getDescription(),
          pensum.getEffectiveFrom(),
          pensum.isActive(),
          new CareerSummary(career.getId(), career.getCode(), career.getName()),
          credits,
          terms);
    }
  }

  public record CareerSummary(Long id, String code, String name) {}

  public record Term(Short number, List<SubjectItem> subjects) {}

  public record SubjectItem(
      Long id,
      String code,
      String name,
      String description,
      Short credits,
      Short weeklyHours,
      boolean mandatory,
      List<Prerequisite> prerequisites) {
    static SubjectItem from(PensumSubject item) {
      var subject = item.getSubject();
      return new SubjectItem(
          subject.getId(),
          subject.getCode(),
          subject.getName(),
          subject.getDescription(),
          subject.getCredits(),
          subject.getWeeklyHours(),
          item.isMandatory(),
          subject.getPrerequisites().stream()
              .map(value -> new Prerequisite(value.getId(), value.getCode(), value.getName()))
              .toList());
    }
  }

  public record Prerequisite(Long id, String code, String name) {}
}
