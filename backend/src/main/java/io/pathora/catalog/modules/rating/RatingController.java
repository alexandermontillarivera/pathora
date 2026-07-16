package io.pathora.catalog.modules.rating;

import io.pathora.catalog.shared.api.ApiResponse;
import io.pathora.catalog.shared.pagination.PageResponse;
import io.pathora.catalog.shared.pagination.PaginationRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1")
@Tag(name = "Valoraciones", description = "Valoraciones de carreras de la comunidad")
public class RatingController {
  private final RatingService service;

  public RatingController(RatingService service) {
    this.service = service;
  }

  @GetMapping("/careers/{careerId}/ratings")
  @Operation(summary = "Obtener el resumen de valoraciones")
  ApiResponse<RatingDto.Summary> summary(@PathVariable Long careerId) {
    return ApiResponse.ok("Valoraciones obtenidas correctamente.", service.summary(careerId));
  }

  @GetMapping("/careers/{careerId}/ratings/me")
  @Operation(summary = "Obtener mi valoración de una carrera")
  @SecurityRequirement(name = "bearerAuth")
  ApiResponse<RatingDto.Response> findMine(
      @PathVariable Long careerId, @AuthenticationPrincipal Jwt jwt) {
    return ApiResponse.ok(
        "Valoración personal obtenida correctamente.", service.findMine(careerId, userId(jwt)));
  }

  @GetMapping("/users/{userId}/ratings")
  @Operation(summary = "Listar las valoraciones públicas de un usuario")
  ApiResponse<PageResponse<RatingDto.Response>> findByUser(
      @PathVariable Long userId, @Valid @ModelAttribute PaginationRequest pagination) {
    return ApiResponse.ok(
        "Valoraciones del usuario obtenidas correctamente.",
        service.findByUser(userId, pagination));
  }

  @PostMapping("/careers/{careerId}/ratings")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Valorar una carrera")
  @SecurityRequirement(name = "bearerAuth")
  ApiResponse<RatingDto.Response> create(
      @PathVariable Long careerId,
      @AuthenticationPrincipal Jwt jwt,
      @Valid @RequestBody RatingDto.Request request) {
    return ApiResponse.ok(
        "Valoración creada correctamente.", service.create(careerId, userId(jwt), request));
  }

  @PutMapping("/ratings/{id}")
  @Operation(summary = "Editar una valoración propia")
  @SecurityRequirement(name = "bearerAuth")
  ApiResponse<RatingDto.Response> update(
      @PathVariable Long id,
      @AuthenticationPrincipal Jwt jwt,
      @Valid @RequestBody RatingDto.Request request) {
    return ApiResponse.ok(
        "Valoración actualizada correctamente.", service.update(id, userId(jwt), request));
  }

  @DeleteMapping("/ratings/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Eliminar una valoración propia")
  @SecurityRequirement(name = "bearerAuth")
  void delete(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
    service.delete(id, userId(jwt));
  }

  private Long userId(Jwt jwt) {
    return Long.valueOf(jwt.getSubject());
  }
}
