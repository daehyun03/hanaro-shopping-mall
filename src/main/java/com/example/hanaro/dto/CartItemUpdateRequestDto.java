package com.example.hanaro.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CartItemUpdateRequestDto(
        @NotNull
        @Min(value = 0, message = "수량은 0 이상이어야 합니다.")
        Integer quantity
) {
}
