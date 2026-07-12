package io.pathora.catalog.modules.saved;

import io.pathora.catalog.entities.SavedCareer;
import io.pathora.catalog.modules.career.CareerService;
import io.pathora.catalog.repositories.*;
import io.pathora.catalog.shared.exception.ResourceNotFoundException;
import io.pathora.catalog.shared.pagination.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SavedService {
  private final SavedCareerRepository saved;
  private final UserRepository users;
  private final CareerRepository careers;
  private final CareerService careerService;

  public SavedService(
      SavedCareerRepository saved,
      UserRepository users,
      CareerRepository careers,
      CareerService careerService) {
    this.saved = saved;
    this.users = users;
    this.careers = careers;
    this.careerService = careerService;
  }

  public PageResponse<SavedDto.Response> list(Long userId, PaginationRequest pagination) {
    return PageResponse.from(
        saved.findAllByUserId(userId, pagination.pageable("createdAt")).map(this::response),
        pagination);
  }

  @Transactional
  public SavedDto.Response add(Long userId, Long careerId) {
    var existing = saved.findByUserIdAndCareerId(userId, careerId);
    if (existing.isPresent()) return response(existing.get());
    var user =
        users.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found."));
    var career =
        careers
            .findById(careerId)
            .orElseThrow(() -> new ResourceNotFoundException("Career not found."));
    return response(saved.save(new SavedCareer(user, career)));
  }

  @Transactional
  public void remove(Long userId, Long careerId) {
    saved.findByUserIdAndCareerId(userId, careerId).ifPresent(saved::delete);
  }

  private SavedDto.Response response(SavedCareer value) {
    return new SavedDto.Response(
        value.getId(), careerService.findById(value.getCareer().getId()), value.getCreatedAt());
  }
}
