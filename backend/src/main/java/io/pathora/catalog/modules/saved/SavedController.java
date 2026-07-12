package io.pathora.catalog.modules.saved;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.pathora.catalog.shared.api.ApiResponse;
import io.pathora.catalog.shared.pagination.PageResponse;
import io.pathora.catalog.shared.pagination.PaginationRequest;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/me/saved-careers")
@SecurityRequirement(name = "bearerAuth")
public class SavedController {
  private final SavedService service;

  public SavedController(SavedService service) {
    this.service = service;
  }

  @GetMapping
  ApiResponse<PageResponse<SavedDto.Response>> list(
      @AuthenticationPrincipal Jwt jwt, @Valid @ModelAttribute PaginationRequest pagination) {
    return ApiResponse.ok("Saved careers retrieved.", service.list(id(jwt), pagination));
  }

  @PostMapping("/{careerId}")
  @ResponseStatus(HttpStatus.CREATED)
  ApiResponse<SavedDto.Response> add(
      @AuthenticationPrincipal Jwt jwt, @PathVariable Long careerId) {
    return ApiResponse.ok("Career saved.", service.add(id(jwt), careerId));
  }

  @DeleteMapping("/{careerId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void remove(@AuthenticationPrincipal Jwt jwt, @PathVariable Long careerId) {
    service.remove(id(jwt), careerId);
  }

  private Long id(Jwt jwt) {
    return Long.valueOf(jwt.getSubject());
  }
}
