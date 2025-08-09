package com.example.hanaro.controller;

import com.example.hanaro.dto.ProductDetailResponseDto;
import com.example.hanaro.dto.ProductResponseDto;
import com.example.hanaro.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Public - Product", description = "공용 상품 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product")
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "상품 목록 조회", description = "검색어를 통해 상품명으로 검색할 수 있습니다.")
    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getProducts(
            @Parameter(description = "검색할 상품명") @RequestParam(required = false) String keyword) {

        List<ProductResponseDto> products = productService.getProducts(keyword);
        return ResponseEntity.ok(products);
    }
    @Operation(summary = "상품 상세 조회", description = "상품 ID를 통해 상품의 상세 정보를 조회합니다.")
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDetailResponseDto> getProductById(
            @Parameter(description = "조회할 상품 ID") @RequestParam Long productId) {

        ProductDetailResponseDto productDetail = productService.getProductById(productId);
        return ResponseEntity.ok(productDetail);
    }

}
