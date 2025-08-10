package com.example.hanaro.controller;

import com.example.hanaro.config.swagger.response.*;
import com.example.hanaro.dto.OrderResponseDto;
import org.springframework.security.core.userdetails.User;
import com.example.hanaro.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User - Order", description = "사용자 주문 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "장바구니 전체 주문 생성", description = "장바구니에 담긴 모든 상품으로 주문을 생성합니다.")
    @ApiResponse(responseCode = "201", description = "주문 생성 성공")
    @Api400Error
    @Api401Error
    @Api403Error
    @Api409Error
    @Api500ErrorGroup
    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrderFromCart(@AuthenticationPrincipal User user) {
        OrderResponseDto responseDto = orderService.createOrderFromCart(user.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
}
