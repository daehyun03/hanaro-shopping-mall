package com.example.hanaro.dto.request.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ProductStockUpdateRequestDto(
        @NotNull(message = "재고 수량은 필수 입력 항목입니다.")
        @Min(value = 0, message = "재고 수량은 0개 이상이어야 합니다.")
        Integer stock
) {
}
