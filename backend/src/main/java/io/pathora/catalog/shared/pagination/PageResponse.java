package io.pathora.catalog.shared.pagination;

import java.util.List;
import org.springframework.data.domain.Page;

public record PageResponse<T>(
    List<T> records,
    int totalPages,
    long totalRecords,
    int page,
    int max,
    String order,
    boolean hasNext,
    boolean hasPrevious) {

  public static <T> PageResponse<T> from(Page<T> result, PaginationRequest request) {
    return new PageResponse<>(
        result.getContent(),
        result.getTotalPages(),
        result.getTotalElements(),
        request.getPage(),
        request.getMax(),
        request.getOrder().name(),
        result.hasNext(),
        result.hasPrevious());
  }
}
