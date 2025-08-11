package com.example.hanaro.controller;

import com.example.hanaro.config.swagger.response.*;
import com.example.hanaro.dto.request.product.ProductCreateRequestDto;
import com.example.hanaro.dto.request.product.ProductStockUpdateRequestDto;
import com.example.hanaro.dto.request.product.ProductUpdateRequestDto;
import com.example.hanaro.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Admin - Product", description = "관리자 상품 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/product")
public class ProductAdminController {

    private final ProductService productService;

    @Operation(summary = "상품 등록", description = "새로운 상품을 등록합니다. ADMIN 권한이 필요합니다.")
    @ApiResponse(responseCode = "201", description = "상품 등록 성공")
    @Api400Error
    @Api401Error
    @Api403Error
    @Api500ErrorGroup
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> createProduct(
            @Valid
            @RequestPart("productInfo") ProductCreateRequestDto requestDto,
            @RequestPart("imageFile") MultipartFile imageFile) {

        productService.createProduct(requestDto, imageFile);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @Operation(summary = "상품 재고 수정", description = "특정 상품의 재고를 수정합니다. ADMIN 권한이 필요합니다.")
    @ApiResponse(responseCode = "200", description = "재고 수정 성공")
    @Api400Error
    @Api401Error
    @Api403Error
    @Api404Error
    @Api500ErrorGroup
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{productId}/stock")
    public ResponseEntity<String> updateProductStock(
            @PathVariable Long productId,
            @Valid @RequestBody ProductStockUpdateRequestDto requestDto) {

        productService.updateProductStock(productId, requestDto);
        return ResponseEntity.status(200).body("상품 재고가 성공적으로 수정되었습니다.");
    }

    @Operation(summary = "상품 삭제", description = "특정 상품을 삭제합니다. ADMIN 권한이 필요합니다.")
    @ApiResponse(responseCode = "204", description = "상품 삭제 성공")
    @Api400Error
    @Api401Error
    @Api403Error
    @Api404Error
    @Api500ErrorGroup
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "상품 정보 수정", description = "특정 상품의 정보를 수정합니다. ADMIN 권한이 필요합니다.")
    @ApiResponse(responseCode = "200", description = "상품 정보 수정 성공")
    @Api400Error
    @Api401Error
    @Api403Error
    @Api500ErrorGroup
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping(value = "/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateProduct(
            @PathVariable Long productId,
            @Valid @RequestPart("productInfo") ProductUpdateRequestDto requestDto,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {

        productService.updateProduct(productId, requestDto, imageFile);
        return ResponseEntity.status(200).body("상품 정보를 수정했습니다.");
    }
}
