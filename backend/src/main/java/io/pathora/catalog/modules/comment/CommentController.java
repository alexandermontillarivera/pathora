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
@Tag(name = "Comments", description = "Structured rich-text career comments")
public class CommentController {
  private final CommentService service;

  public CommentController(CommentService service) {
    this.service = service;
  }

  @GetMapping("/careers/{careerId}/comments")
  @Operation(summary = "List career comments")
  ApiResponse<PageResponse<CommentDto.Response>> findAll(
      @PathVariable Long careerId, @Valid @ModelAttribute PaginationRequest pagination) {
    return ApiResponse.ok("Comments retrieved.", service.findAll(careerId, pagination));
  }

  @PostMapping("/careers/{careerId}/comments")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Comment on a career")
  @SecurityRequirement(name = "bearerAuth")
  ApiResponse<CommentDto.Response> create(
      @PathVariable Long careerId,
      @AuthenticationPrincipal Jwt jwt,
      @Valid @RequestBody CommentDto.Request request) {
    return ApiResponse.ok("Comment created.", service.create(careerId, userId(jwt), request));
  }

  @PutMapping("/comments/{id}")
  @Operation(summary = "Edit your comment")
  @SecurityRequirement(name = "bearerAuth")
  ApiResponse<CommentDto.Response> update(
      @PathVariable Long id,
      @AuthenticationPrincipal Jwt jwt,
      @Valid @RequestBody CommentDto.Request request) {
    return ApiResponse.ok("Comment updated.", service.update(id, userId(jwt), request));
  }

  @DeleteMapping("/comments/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Delete your comment")
  @SecurityRequirement(name = "bearerAuth")
  void delete(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
    service.delete(id, userId(jwt));
  }

  @GetMapping("/comments")
  @Operation(summary = "List recent community comments")
  ApiResponse<PageResponse<CommentDto.Response>> findRecent(
      @Valid @ModelAttribute PaginationRequest pagination) {
    return ApiResponse.ok("Community comments retrieved.", service.findRecent(pagination));
  }

  @GetMapping("/users/{userId}/comments")
  @Operation(summary = "List a user's public comments")
  ApiResponse<PageResponse<CommentDto.Response>> findByUser(
      @PathVariable Long userId, @Valid @ModelAttribute PaginationRequest pagination) {
    return ApiResponse.ok("User comments retrieved.", service.findByUser(userId, pagination));
  }

  @PostMapping("/comments/{id}/replies")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Reply to a comment")
  @SecurityRequirement(name = "bearerAuth")
  ApiResponse<CommentDto.Response> reply(
      @PathVariable Long id,
      @AuthenticationPrincipal Jwt jwt,
      @Valid @RequestBody CommentDto.Request request) {
    return ApiResponse.ok("Reply created.", service.reply(id, userId(jwt), request));
  }

  @PutMapping("/comments/{id}/vote")
  @Operation(summary = "Mark a comment as useful or not useful")
  @SecurityRequirement(name = "bearerAuth")
  ApiResponse<CommentDto.VoteResponse> vote(
      @PathVariable Long id,
      @AuthenticationPrincipal Jwt jwt,
      @Valid @RequestBody CommentDto.VoteRequest request) {
    return ApiResponse.ok("Vote saved.", service.vote(id, userId(jwt), request));
  }

  @DeleteMapping("/comments/{id}/vote")
  @Operation(summary = "Remove your comment vote")
  @SecurityRequirement(name = "bearerAuth")
  ApiResponse<CommentDto.VoteResponse> removeVote(
      @PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
    return ApiResponse.ok("Vote removed.", service.removeVote(id, userId(jwt)));
  }

  private Long userId(Jwt jwt) {
    return Long.valueOf(jwt.getSubject());
  }
}
