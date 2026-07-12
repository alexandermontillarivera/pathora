package io.pathora.catalog.modules.user;

import io.pathora.catalog.shared.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/users")
@Tag(name = "Users", description = "Public profiles and authenticated profile settings")
public class UserController {
  private final UserService service;

  public UserController(UserService service) {
    this.service = service;
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get a public user profile")
  ApiResponse<UserDto.Response> findOne(@PathVariable Long id) {
    return ApiResponse.ok("User retrieved.", service.findById(id));
  }

  @PutMapping("/me")
  @Operation(summary = "Update the authenticated profile")
  @SecurityRequirement(name = "bearerAuth")
  ApiResponse<UserDto.Response> update(
      @AuthenticationPrincipal Jwt jwt, @Valid @RequestBody UserDto.UpdateRequest request) {
    return ApiResponse.ok(
        "Profile updated.", service.update(Long.valueOf(jwt.getSubject()), request));
  }
}
