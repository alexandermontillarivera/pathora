package io.pathora.catalog.modules.pensum;

import io.pathora.catalog.shared.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/pensums")
@Tag(name = "Pensums", description = "Planes académicos organizados por períodos y materias")
public class PensumController {
  private final PensumService service;

  public PensumController(PensumService service) {
    this.service = service;
  }

  @GetMapping("/career/{careerId}")
  @Operation(summary = "Obtener el pensum activo de una carrera")
  ApiResponse<PensumDto.Response> findByCareer(@PathVariable Long careerId) {
    return ApiResponse.ok("Pensum obtenido correctamente.", service.findActiveByCareer(careerId));
  }
}
