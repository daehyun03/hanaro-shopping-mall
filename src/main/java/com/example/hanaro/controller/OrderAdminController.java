package com.example.hanaro.controller;

import com.example.hanaro.config.swagger.response.Api401Error;
import com.example.hanaro.config.swagger.response.Api403Error;
import com.example.hanaro.config.swagger.response.Api404Error;
import com.example.hanaro.config.swagger.response.Api500ErrorGroup;
import com.example.hanaro.dto.response.order.OrderResponseDto;
import com.example.hanaro.enums.OrderStatus;
import com.example.hanaro.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Admin - Order", description = "관리자 주문 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/order")
@PreAuthorize("hasRole('ADMIN')")
public class OrderAdminController {

    private final OrderService orderService;

    @Operation(summary = "전체 주문 내역 조회", description = "모든 사용자의 주문 내역을 최신순으로 조회합니다. " +
            "사용자 이메일, 주문 상태, 상품 아이디로 필터링할 수 있습니다.")
    @ApiResponse(responseCode = "200", description = "전체 주문 내역 조회 성공")
    @Api401Error
    @Api403Error
    @Api500ErrorGroup
    @GetMapping("/list")
    public ResponseEntity<List<OrderResponseDto>> getAllOrders(
            @Parameter(description = "검색할 사용자 이메일이 주문한 주문들을 조회합니다.") @RequestParam(required = false) String userEmail,
            @Parameter(description = "검색할 주문 상태인 주문들을 조회합니다.") @RequestParam(required = false) OrderStatus orderStatus,
            @Parameter(description = "검색할 상품 아이디가 포함된 주문들을 조회합니다") @RequestParam(required = false) Long productId) {
        List<OrderResponseDto> orders = orderService.getAllOrders(userEmail, orderStatus, productId);
        return ResponseEntity.ok(orders);
    }

    @Operation(summary = "주문 상태 강제 변경", description = "특정 주문의 상태를 관리자가 직접 변경합니다.")
    @ApiResponse(responseCode = "200", description = "주문 상태 변경 성공")
    @Api401Error
    @Api403Error
    @Api404Error
    @Api500ErrorGroup
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<String> updateOrderStatus(
            @PathVariable Long orderId,
            @Parameter(description = "변경할 상태를 선택합니다.") @RequestParam(required = true) OrderStatus orderStatus) {

        orderService.updateOrderStatus(orderId, orderStatus);
        return ResponseEntity.ok().body("주문 ID" + orderId + "가 주문 상태가 " + orderStatus + "로 변경되었습니다.");
    }
}
