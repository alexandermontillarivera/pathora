package io.pathora.catalog.modules.community;

import io.pathora.catalog.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CommunityService {
  private final CareerCommentRepository comments;
  private final RatingRepository ratings;

  public CommunityService(CareerCommentRepository comments, RatingRepository ratings) {
    this.comments = comments;
    this.ratings = ratings;
  }

  public CommunityDto.Stats stats() {
    return new CommunityDto.Stats(
        Math.round(ratings.globalAverage() * 100.0) / 100.0,
        comments.countByParentIsNull(),
        comments.countDistinctCareers());
  }
}
