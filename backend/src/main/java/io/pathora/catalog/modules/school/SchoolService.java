package io.pathora.catalog.modules.school;

import io.pathora.catalog.repositories.CareerRepository;
import io.pathora.catalog.repositories.SchoolRepository;
import io.pathora.catalog.shared.pagination.PageResponse;
import io.pathora.catalog.shared.pagination.PaginationRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SchoolService {
  private final SchoolRepository repository;
  private final CareerRepository careers;

  public SchoolService(SchoolRepository repository, CareerRepository careers) {
    this.repository = repository;
    this.careers = careers;
  }

  public PageResponse<SchoolDto.Response> findAll(PaginationRequest pagination) {
    var page = repository.findAll(pagination.pageable("name")).map(this::response);
    return PageResponse.from(page, pagination);
  }

  public SchoolDto.Response findById(Long id) {
    return response(
        repository
            .findById(id)
            .orElseThrow(
                () ->
                    new io.pathora.catalog.shared.exception.ResourceNotFoundException(
                        "No se encontró la escuela.")));
  }

  private SchoolDto.Response response(io.pathora.catalog.entities.School school) {
    return SchoolDto.Response.from(school, careers.countBySchoolId(school.getId()));
  }
}
