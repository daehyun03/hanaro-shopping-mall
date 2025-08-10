package com.example.hanaro.service;

import com.example.hanaro.dto.CartItemAddRequestDto;
import com.example.hanaro.dto.CartResponseDto;
import com.example.hanaro.entity.*;
import com.example.hanaro.exception.CustomException;
import com.example.hanaro.enums.ErrorCode;
import com.example.hanaro.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class CartService {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    public void addCartItem(String userEmail, CartItemAddRequestDto requestDto) {
        // 사용자 및 상품 조회
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Product product = productRepository.findById(requestDto.productId())
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        // 사용자의 장바구니 조회 (없으면 생성)
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder().user(user).build();
                    return cartRepository.save(newCart);
                });

        // 재고 확인
        if (product.getStock() < requestDto.quantity()) {
            throw new CustomException(ErrorCode.STOCK_NOT_ENOUGH);
        }

        // 장바구니에 이미 상품이 있는지 확인
        cartItemRepository.findByCartAndProduct(cart, product)
                .ifPresentOrElse(
                        cartItem -> {
                            cartItem.addQuantity(requestDto.quantity());
                            if (product.getStock() < cartItem.getQuantity()) {
                                throw new CustomException(ErrorCode.STOCK_NOT_ENOUGH);
                            }
                        },
                        () -> {
                            CartItem newCartItem = CartItem.builder()
                                    .cart(cart)
                                    .product(product)
                                    .quantity(requestDto.quantity())
                                    .build();
                            cartItemRepository.save(newCartItem);
                        }
                );

        log.info("장바구니에 상품이 추가되었습니다. 사용자: {}, 상품: {}, 수량: {}", userEmail, product.getName(), requestDto.quantity());
    }

    public void deleteCartItem(String userEmail, Long cartItemId) {
        // cartItemId로 장바구니 아이템을 조회
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new CustomException(ErrorCode.CART_ITEM_NOT_FOUND));

        // 현재 로그인된 사용자와 장바구니 아이템을 담은 사용자가 같은지 확인
        if (!cartItem.getCart().getUser().getEmail().equals(userEmail)) {
            throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
        }

        // 아이템 삭제
        log.info("장바구니 아이템이 삭제되었습니다. 사용자: {}, 아이템 ID: {}", userEmail, cartItemId);
        cartItemRepository.delete(cartItem);
    }

    @Transactional(readOnly = true)
    public CartResponseDto getMyCart(String userEmail) {
        // 사용자 조회
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 사용자의 장바구니 조회 (없으면 빈 장바구니 DTO 반환)
        Cart cart = cartRepository.findByUser(user)
                .orElse(null); // 장바구니가 없을 수도 있으므로 null 허용

        if (cart == null || cart.getCartItems().isEmpty()) {
            // 장바구니가 비어있는 경우
            return new CartResponseDto(new Cart());
        }

        // CartResponseDto로 변환하여 반환
        log.info("장바구니 조회 성공. 사용자: {}", userEmail);
        return new CartResponseDto(cart);
    }

    public void updateCartItemQuantity(String userEmail, Long cartItemId, Integer quantity) {
        // cartItemId로 장바구니 아이템을 조회
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new CustomException(ErrorCode.CART_ITEM_NOT_FOUND));

        // 현재 로그인된 사용자와 장바구니 아이템을 담은 사용자가 같은지 확인
        if (!cartItem.getCart().getUser().getEmail().equals(userEmail)) {
            throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
        }

        // 요청된 수량이 0이면 아이템 삭제
        if (quantity == 0) {
            log.info("장바구니 아이템 수량이 0으로 설정되어 삭제됩니다. 사용자: {}, 아이템 ID: {}", userEmail, cartItemId);
            cartItemRepository.delete(cartItem);
            return;
        }

        // 상품 재고 확인
        Product product = cartItem.getProduct();
        if (product.getStock() < quantity) {
            throw new CustomException(ErrorCode.STOCK_NOT_ENOUGH);
        }

        cartItem.updateQuantity(quantity);
        log.info("장바구니 아이템 수량이 업데이트되었습니다. 사용자: {}, 아이템 ID: {}, 새로운 수량: {}", userEmail, cartItemId, quantity);
    }
}