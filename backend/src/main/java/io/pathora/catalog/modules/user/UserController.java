package io.pathora.catalog.modules.user;

import io.pathora.catalog.shared.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/users")
@Tag(name = "Usuarios", description = "Perfiles públicos y configuración del perfil autenticado")
public class UserController {
  private final UserService service;

  public UserController(UserService service) {
    this.service = service;
  }

  @GetMapping("/{id}")
  @Operation(summary = "Obtener el perfil público de un usuario")
  ApiResponse<UserDto.Response> findOne(@PathVariable Long id) {
    return ApiResponse.ok("Usuario obtenido correctamente.", service.findById(id));
  }

  @PutMapping("/me")
  @Operation(summary = "Actualizar el perfil autenticado")
  @SecurityRequirement(name = "bearerAuth")
  ApiResponse<UserDto.Response> update(
      @AuthenticationPrincipal Jwt jwt, @Valid @RequestBody UserDto.UpdateRequest request) {
    return ApiResponse.ok(
        "Perfil actualizado correctamente.",
        service.update(Long.valueOf(jwt.getSubject()), request));
  }

  @DeleteMapping("/me")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Eliminar definitivamente la cuenta autenticada")
  @SecurityRequirement(name = "bearerAuth")
  void delete(@AuthenticationPrincipal Jwt jwt) {
    service.delete(Long.valueOf(jwt.getSubject()));
  }
}
