package com.example.hanaro.controller;

import com.example.hanaro.config.swagger.response.Api401Error;
import com.example.hanaro.config.swagger.response.Api403Error;
import com.example.hanaro.config.swagger.response.Api500ErrorGroup;
import com.example.hanaro.dto.OrderResponseDto;
import com.example.hanaro.enums.OrderStatus;
import com.example.hanaro.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Admin - Order", description = "관리자 주문 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/order")
@PreAuthorize("hasRole('ADMIN')")
public class OrderAdminController {

    private final OrderService orderService;

    @Operation(summary = "전체 주문 내역 조회", description = "모든 사용자의 주문 내역을 최신순으로 조회합니다.")
    @ApiResponse(responseCode = "200", description = "전체 주문 내역 조회 성공")
    @Api401Error
    @Api403Error
    @Api500ErrorGroup
    @GetMapping("/list")
    public ResponseEntity<List<OrderResponseDto>> getAllOrders(
            @Parameter(description = "검색할 사용자 이메일") @RequestParam(required = false) String userEmail,
            @Parameter(description = "검색할 주문 상태") @RequestParam(required = false) OrderStatus orderStatus) {

        List<OrderResponseDto> orders = orderService.getAllOrders(userEmail, orderStatus);
        return ResponseEntity.ok(orders);
    }
}
