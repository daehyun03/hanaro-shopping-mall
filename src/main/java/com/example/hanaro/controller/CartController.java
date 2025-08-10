package com.example.hanaro.controller;

import com.example.hanaro.config.swagger.response.*;
import com.example.hanaro.dto.CartItemAddRequestDto;
import com.example.hanaro.dto.CartItemUpdateRequestDto;
import com.example.hanaro.dto.CartResponseDto;
import com.example.hanaro.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User - Cart", description = "사용자 장바구니 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    @Operation(summary = "장바구니에 상품 추가", description = "상품 ID와 수량을 받아 장바구니에 추가합니다.")
    @ApiResponse(responseCode = "200", description = "상품 추가 성공")
    @Api401Error
    @Api403Error
    @Api404Error
    @Api409Error
    @Api500ErrorGroup
    @PreAuthorize("hasAnyRole('USER')")
    @PostMapping("/item")
    public ResponseEntity<String> addCartItem(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CartItemAddRequestDto requestDto) {

        cartService.addCartItem(user.getUsername(), requestDto);
        return ResponseEntity.status(200).body("장바구니에 상품이 성공적으로 추가되었습니다.");
    }

    @Operation(summary = "장바구니에서 특정 상품 삭제", description = "장바구니 아이템 ID를 받아 해당 상품을 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "상품 삭제 성공")
    @Api401Error
    @Api403Error
    @Api404Error
    @Api500ErrorGroup
    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/item/{cartItemId}")
    public ResponseEntity<Void> deleteCartItem(
            @AuthenticationPrincipal User user,
            @PathVariable Long cartItemId) {

        cartService.deleteCartItem(user.getUsername(), cartItemId);
        return ResponseEntity.status(204).build();
    }

    @Operation(summary = "내 장바구니 조회", description = "현재 로그인된 사용자의 장바구니 상품 목록과 총액을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "장바구니 조회 성공")
    @Api401Error
    @Api403Error
    @Api500ErrorGroup
    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public ResponseEntity<CartResponseDto> getMyCart(@AuthenticationPrincipal User user) {
        CartResponseDto myCart = cartService.getMyCart(user.getUsername());
        return ResponseEntity.ok(myCart);
    }

    @Operation(summary = "장바구니 상품 수량 수정", description = "특정 상품의 수량을 수정합니다. 수량이 0이면 자동 삭제됩니다.")
    @ApiResponse(responseCode = "200", description = "수량 수정 성공")
    @Api401Error
    @Api403Error
    @Api404Error
    @Api409Error
    @Api500ErrorGroup
    @PreAuthorize("hasRole('USER')")
    @PatchMapping("/item/{cartItemId}")
    public ResponseEntity<String> updateCartItemQuantity(
            @AuthenticationPrincipal User user,
            @PathVariable Long cartItemId,
            @Valid @RequestBody CartItemUpdateRequestDto requestDto) {

        cartService.updateCartItemQuantity(user.getUsername(), cartItemId, requestDto.quantity());
        return ResponseEntity.status(200).body("장바구니 상품 수량이 성공적으로 수정되었습니다.");
    }
}