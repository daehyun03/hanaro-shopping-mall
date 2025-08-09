package com.example.hanaro.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductUpdateRequestDto(
        @NotBlank(message = "상품명은 필수 입력 항목입니다.")
        String name,

        @NotNull(message = "가격은 필수 입력 항목입니다.")
        @Min(value = 0, message = "가격은 0원 이상이어야 합니다.")
        Integer price,

        @NotNull(message = "재고 수량은 필수 입력 항목입니다.")
        @Min(value = 0, message = "재고 수량은 0개 이상이어야 합니다.")
        Integer stock
) {
}
