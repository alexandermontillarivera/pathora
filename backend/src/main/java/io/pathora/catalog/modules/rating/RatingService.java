package io.pathora.catalog.modules.rating;

import io.pathora.catalog.entities.Rating;
import io.pathora.catalog.repositories.CareerRepository;
import io.pathora.catalog.repositories.RatingRepository;
import io.pathora.catalog.repositories.UserRepository;
import io.pathora.catalog.shared.exception.*;
import io.pathora.catalog.shared.pagination.PageResponse;
import io.pathora.catalog.shared.pagination.PaginationRequest;
import java.util.LinkedHashMap;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class RatingService {
  private final RatingRepository ratings;
  private final UserRepository users;
  private final CareerRepository careers;

  public RatingService(RatingRepository ratings, UserRepository users, CareerRepository careers) {
    this.ratings = ratings;
    this.users = users;
    this.careers = careers;
  }

  public RatingDto.Summary summary(Long careerId) {
    ensureCareer(careerId);
    var values = ratings.findAllByCareerId(careerId);
    var distribution = new LinkedHashMap<Integer, Long>();
    for (int i = 1; i <= 5; i++) distribution.put(i, 0L);
    values.forEach(r -> distribution.compute(r.getValue().intValue(), (k, v) -> v + 1));
    double average = values.stream().mapToInt(Rating::getValue).average().orElse(0);
    return new RatingDto.Summary(values.size(), Math.round(average * 100.0) / 100.0, distribution);
  }

  public PageResponse<RatingDto.Response> findByUser(Long userId, PaginationRequest pagination) {
    user(userId);
    var page =
        ratings
            .findAllByUserId(userId, pagination.pageable("createdAt"))
            .map(RatingDto.Response::from);
    return PageResponse.from(page, pagination);
  }

  public RatingDto.Response findMine(Long careerId, Long userId) {
    ensureCareer(careerId);
    return ratings
        .findByUserIdAndCareerId(userId, careerId)
        .map(RatingDto.Response::from)
        .orElse(null);
  }

  @Transactional
  public RatingDto.Response create(Long careerId, Long userId, RatingDto.Request request) {
    var existing = ratings.findByUserIdAndCareerId(userId, careerId);
    if (existing.isPresent()) {
      existing.get().update(request.value());
      return RatingDto.Response.from(existing.get());
    }
    var rating = new Rating(user(userId), ensureCareer(careerId), request.value());
    return RatingDto.Response.from(ratings.save(rating));
  }

  @Transactional
  public RatingDto.Response update(Long id, Long userId, RatingDto.Request request) {
    var rating = owned(id, userId);
    rating.update(request.value());
    return RatingDto.Response.from(rating);
  }

  @Transactional
  public void delete(Long id, Long userId) {
    ratings.delete(owned(id, userId));
  }

  private Rating owned(Long id, Long userId) {
    var rating =
        ratings
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("No se encontró la valoración."));
    if (!rating.getUser().getId().equals(userId))
      throw new ForbiddenException("Solo puedes modificar tus propias valoraciones.");
    return rating;
  }

  private io.pathora.catalog.entities.User user(Long id) {
    return users
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("No se encontró el usuario."));
  }

  private io.pathora.catalog.entities.Career ensureCareer(Long id) {
    return careers
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("No se encontró la carrera."));
  }
}
