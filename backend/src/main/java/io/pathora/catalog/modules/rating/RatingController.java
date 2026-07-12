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
@Tag(name = "Ratings", description = "Career rating community")
public class RatingController {
  private final RatingService service;

  public RatingController(RatingService service) {
    this.service = service;
  }

  @GetMapping("/careers/{careerId}/ratings")
  @Operation(summary = "Get rating summary")
  ApiResponse<RatingDto.Summary> summary(@PathVariable Long careerId) {
    return ApiResponse.ok("Ratings retrieved.", service.summary(careerId));
  }

  @GetMapping("/users/{userId}/ratings")
  @Operation(summary = "List a user's public ratings")
  ApiResponse<PageResponse<RatingDto.Response>> findByUser(
      @PathVariable Long userId, @Valid @ModelAttribute PaginationRequest pagination) {
    return ApiResponse.ok("User ratings retrieved.", service.findByUser(userId, pagination));
  }

  @PostMapping("/careers/{careerId}/ratings")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Rate a career")
  @SecurityRequirement(name = "bearerAuth")
  ApiResponse<RatingDto.Response> create(
      @PathVariable Long careerId,
      @AuthenticationPrincipal Jwt jwt,
      @Valid @RequestBody RatingDto.Request request) {
    return ApiResponse.ok("Rating created.", service.create(careerId, userId(jwt), request));
  }

  @PutMapping("/ratings/{id}")
  @Operation(summary = "Edit your rating")
  @SecurityRequirement(name = "bearerAuth")
  ApiResponse<RatingDto.Response> update(
      @PathVariable Long id,
      @AuthenticationPrincipal Jwt jwt,
      @Valid @RequestBody RatingDto.Request request) {
    return ApiResponse.ok("Rating updated.", service.update(id, userId(jwt), request));
  }

  @DeleteMapping("/ratings/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Delete your rating")
  @SecurityRequirement(name = "bearerAuth")
  void delete(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
    service.delete(id, userId(jwt));
  }

  private Long userId(Jwt jwt) {
    return Long.valueOf(jwt.getSubject());
  }
}
