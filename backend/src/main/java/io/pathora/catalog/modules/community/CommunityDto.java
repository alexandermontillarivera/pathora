package io.pathora.catalog.modules.community;

public final class CommunityDto {
  private CommunityDto() {}

  public record Stats(double averageRating, long comments, long careers) {}
}
