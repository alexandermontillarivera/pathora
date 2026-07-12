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
@Tag(name = "Schools", description = "School and faculty catalog")
public class SchoolController {
  private final SchoolService service;

  public SchoolController(SchoolService service) {
    this.service = service;
  }

  @GetMapping
  @Operation(summary = "List schools")
  @SuppressWarnings("unused")
  ApiResponse<PageResponse<SchoolDto.Response>> findAll(
      @Valid @ModelAttribute PaginationRequest pagination) {
    return ApiResponse.ok("Schools retrieved.", service.findAll(pagination));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get school details")
  ApiResponse<SchoolDto.Response> findOne(@PathVariable Long id) {
    return ApiResponse.ok("School retrieved.", service.findById(id));
  }
}
