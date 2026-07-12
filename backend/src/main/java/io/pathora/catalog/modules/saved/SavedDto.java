package io.pathora.catalog.modules.saved;

import io.pathora.catalog.modules.career.CareerDto;
import java.time.Instant;

public final class SavedDto {
  private SavedDto() {}

  public record Response(Long id, CareerDto.Response career, Instant createdAt) {}
}
