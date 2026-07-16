package io.pathora.catalog.modules.school;

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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/schools")
@Tag(name = "Escuelas", description = "Catálogo de escuelas y facultades")
public class SchoolController {
  private final SchoolService service;

  public SchoolController(SchoolService service) {
    this.service = service;
  }

  @GetMapping
  @Operation(summary = "Listar escuelas")
  @SuppressWarnings("unused")
  ApiResponse<PageResponse<SchoolDto.Response>> findAll(
      @Valid @ModelAttribute PaginationRequest pagination) {
    return ApiResponse.ok("Escuelas obtenidas correctamente.", service.findAll(pagination));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Obtener el detalle de una escuela")
  ApiResponse<SchoolDto.Response> findOne(@PathVariable Long id) {
    return ApiResponse.ok("Escuela obtenida correctamente.", service.findById(id));
  }
}
