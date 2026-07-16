package io.pathora.catalog.modules.pensum;

import io.pathora.catalog.repositories.PensumRepository;
import io.pathora.catalog.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PensumService {
  private final PensumRepository repository;

  public PensumService(PensumRepository repository) {
    this.repository = repository;
  }

  public PensumDto.Response findActiveByCareer(Long careerId) {
    return repository
        .findByCareerIdAndActiveTrue(careerId)
        .map(PensumDto.Response::from)
        .orElseThrow(
            () ->
                new ResourceNotFoundException(
                    "No se encontró un pensum activo para esta carrera."));
  }
}
