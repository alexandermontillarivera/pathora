package io.pathora.catalog.modules.community;

import io.pathora.catalog.shared.api.ApiResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/community")
public class CommunityController {
  private final CommunityService service;

  public CommunityController(CommunityService service) {
    this.service = service;
  }

  @GetMapping("/stats")
  ApiResponse<CommunityDto.Stats> stats() {
    return ApiResponse.ok("Estadísticas de la comunidad obtenidas correctamente.", service.stats());
  }
}
