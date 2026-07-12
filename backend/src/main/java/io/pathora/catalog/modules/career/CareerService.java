package io.pathora.catalog.modules.career;

import io.pathora.catalog.entities.Career;
import io.pathora.catalog.enums.CareerStatus;
import io.pathora.catalog.enums.StudyMode;
import io.pathora.catalog.repositories.CareerRepository;
import io.pathora.catalog.repositories.CareerSpecifications;
import io.pathora.catalog.repositories.RatingRepository;
import io.pathora.catalog.shared.exception.ResourceNotFoundException;
import io.pathora.catalog.shared.pagination.PageResponse;
import io.pathora.catalog.shared.pagination.PaginationRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CareerService {
  private final CareerRepository repository;
  private final RatingRepository ratings;

  public CareerService(CareerRepository repository, RatingRepository ratings) {
    this.repository = repository;
    this.ratings = ratings;
  }

  public PageResponse<CareerDto.Response> findAll(
      String name,
      Long schoolId,
      StudyMode studyMode,
      CareerStatus status,
      PaginationRequest pagination) {
    var spec =
        CareerSpecifications.nameContains(name)
            .and(CareerSpecifications.schoolIs(schoolId))
            .and(CareerSpecifications.studyModeIs(studyMode))
            .and(CareerSpecifications.statusIs(status));
    var page = repository.findAll(spec, pagination.pageable("name")).map(this::response);
    return PageResponse.from(page, pagination);
  }

  public CareerDto.Response findById(Long id) {
    return response(entity(id));
  }

  private Career entity(Long id) {
    return repository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Career not found."));
  }

  private CareerDto.Response response(Career career) {
    return CareerDto.Response.from(
        career, ratings.averageByCareerId(career.getId()), ratings.countByCareerId(career.getId()));
  }
}
