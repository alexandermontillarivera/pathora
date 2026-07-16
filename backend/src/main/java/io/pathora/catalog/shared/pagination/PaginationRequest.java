package io.pathora.catalog.shared.pagination;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PaginationRequest {
  @Min(value = 1, message = "La página debe ser al menos 1") private int page = 1;

  @Min(value = 1, message = "El máximo por página debe ser al menos 1") @Max(value = 100, message = "El máximo por página no puede superar 100") private int max = 20;

  private Sort.Direction order = Sort.Direction.ASC;

  public int getPage() {
    return page;
  }

  public void setPage(int page) {
    this.page = page;
  }

  public int getMax() {
    return max;
  }

  public void setMax(int max) {
    this.max = max;
  }

  public Sort.Direction getOrder() {
    return order;
  }

  public void setOrder(Sort.Direction order) {
    this.order = order;
  }

  public Pageable pageable(String sortProperty) {
    return PageRequest.of(page - 1, max, Sort.by(order, sortProperty));
  }
}
