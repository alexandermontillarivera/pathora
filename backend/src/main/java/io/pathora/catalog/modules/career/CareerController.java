package io.pathora.catalog.modules.career;

import io.pathora.catalog.enums.CareerStatus;
import io.pathora.catalog.enums.StudyMode;
import io.pathora.catalog.shared.api.ApiResponse;
import io.pathora.catalog.shared.pagination.PageResponse;
import io.pathora.catalog.shared.pagination.PaginationRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/careers")
@Tag(name = "Carreras", description = "Consulta del catálogo académico de carreras")
public class CareerController {
  private final CareerService service;

  public CareerController(CareerService service) {
    this.service = service;
  }

  @GetMapping
  @Operation(summary = "Listar carreras, buscar por nombre y filtrar por escuela")
  ApiResponse<PageResponse<CareerDto.Response>> findAll(
      @RequestParam(required = false) String name,
      @RequestParam(required = false) Long schoolId,
      @RequestParam(required = false) StudyMode studyMode,
      @RequestParam(required = false) CareerStatus status,
      @Valid @ModelAttribute PaginationRequest pagination) {
    var data = service.findAll(name, schoolId, studyMode, status, pagination);
    var message =
        data.records().isEmpty()
            ? "No se encontraron carreras con los criterios indicados."
            : "Carreras obtenidas correctamente.";
    return ApiResponse.ok(message, data);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Obtener el detalle de una carrera")
  ApiResponse<CareerDto.Response> findOne(@PathVariable Long id) {
    return ApiResponse.ok("Carrera obtenida correctamente.", service.findById(id));
  }
}
