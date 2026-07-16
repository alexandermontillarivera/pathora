package io.pathora.catalog.modules.comment;

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
import org.springframework.web.bind.annotation.ModelAttribute;

@RestController
@RequestMapping("/v1")
@Tag(name = "Comentarios", description = "Comentarios enriquecidos sobre carreras")
public class CommentController {
  private final CommentService service;

  public CommentController(CommentService service) {
    this.service = service;
  }

  @GetMapping("/careers/{careerId}/comments")
  @Operation(summary = "Listar los comentarios de una carrera")
  ApiResponse<PageResponse<CommentDto.Response>> findAll(
      @PathVariable Long careerId,
      @Valid @ModelAttribute PaginationRequest pagination,
      @AuthenticationPrincipal Jwt jwt) {
    return ApiResponse.ok(
        "Comentarios obtenidos correctamente.",
        service.findAll(careerId, pagination, optionalUserId(jwt)));
  }

  @PostMapping("/careers/{careerId}/comments")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Comentar sobre una carrera")
  @SecurityRequirement(name = "bearerAuth")
  ApiResponse<CommentDto.Response> create(
      @PathVariable Long careerId,
      @AuthenticationPrincipal Jwt jwt,
      @Valid @RequestBody CommentDto.Request request) {
    return ApiResponse.ok(
        "Comentario creado correctamente.", service.create(careerId, userId(jwt), request));
  }

  @PutMapping("/comments/{id}")
  @Operation(summary = "Editar un comentario propio")
  @SecurityRequirement(name = "bearerAuth")
  ApiResponse<CommentDto.Response> update(
      @PathVariable Long id,
      @AuthenticationPrincipal Jwt jwt,
      @Valid @RequestBody CommentDto.Request request) {
    return ApiResponse.ok(
        "Comentario actualizado correctamente.", service.update(id, userId(jwt), request));
  }

  @GetMapping("/comments/{id}")
  @Operation(summary = "Obtener un comentario")
  ApiResponse<CommentDto.Response> findOne(
      @PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
    return ApiResponse.ok(
        "Comentario obtenido correctamente.", service.findOne(id, optionalUserId(jwt)));
  }

  @DeleteMapping("/comments/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Eliminar un comentario propio")
  @SecurityRequirement(name = "bearerAuth")
  void delete(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
    service.delete(id, userId(jwt));
  }

  @GetMapping("/comments")
  @Operation(summary = "Listar los comentarios recientes de la comunidad")
  ApiResponse<PageResponse<CommentDto.Response>> findRecent(
      @Valid @ModelAttribute PaginationRequest pagination, @AuthenticationPrincipal Jwt jwt) {
    return ApiResponse.ok(
        "Comentarios de la comunidad obtenidos correctamente.",
        service.findRecent(pagination, optionalUserId(jwt)));
  }

  @GetMapping("/users/{userId}/comments")
  @Operation(summary = "Listar los comentarios públicos de un usuario")
  ApiResponse<PageResponse<CommentDto.Response>> findByUser(
      @PathVariable Long userId,
      @Valid @ModelAttribute PaginationRequest pagination,
      @AuthenticationPrincipal Jwt jwt) {
    return ApiResponse.ok(
        "Comentarios del usuario obtenidos correctamente.",
        service.findByUser(userId, pagination, optionalUserId(jwt)));
  }

  @PostMapping("/comments/{id}/replies")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Responder a un comentario")
  @SecurityRequirement(name = "bearerAuth")
  ApiResponse<CommentDto.Response> reply(
      @PathVariable Long id,
      @AuthenticationPrincipal Jwt jwt,
      @Valid @RequestBody CommentDto.Request request) {
    return ApiResponse.ok(
        "Respuesta creada correctamente.", service.reply(id, userId(jwt), request));
  }

  @PutMapping("/comments/{id}/vote")
  @Operation(summary = "Marcar un comentario como útil o no útil")
  @SecurityRequirement(name = "bearerAuth")
  ApiResponse<CommentDto.VoteResponse> vote(
      @PathVariable Long id,
      @AuthenticationPrincipal Jwt jwt,
      @Valid @RequestBody CommentDto.VoteRequest request) {
    return ApiResponse.ok("Voto guardado correctamente.", service.vote(id, userId(jwt), request));
  }

  @DeleteMapping("/comments/{id}/vote")
  @Operation(summary = "Eliminar el voto de un comentario")
  @SecurityRequirement(name = "bearerAuth")
  ApiResponse<CommentDto.VoteResponse> removeVote(
      @PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
    return ApiResponse.ok("Voto eliminado correctamente.", service.removeVote(id, userId(jwt)));
  }

  private Long userId(Jwt jwt) {
    return Long.valueOf(jwt.getSubject());
  }

  private Long optionalUserId(Jwt jwt) {
    return jwt == null ? null : userId(jwt);
  }
}
