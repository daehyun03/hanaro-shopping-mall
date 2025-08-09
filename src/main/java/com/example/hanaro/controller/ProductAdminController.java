package com.example.hanaro.controller;

import com.example.hanaro.config.swagger.response.Api400Error;
import com.example.hanaro.config.swagger.response.Api401Error;
import com.example.hanaro.config.swagger.response.Api403Error;
import com.example.hanaro.config.swagger.response.Api500ErrorGroup;
import com.example.hanaro.dto.ProductCreateRequestDto;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
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
    public ResponseEntity<String> createProduct(
            @Valid
            @RequestPart("productInfo") ProductCreateRequestDto requestDto,
            @RequestPart("imageFile") MultipartFile imageFile) {

        productService.createProduct(requestDto, imageFile);
        return ResponseEntity.status(HttpStatus.CREATED).body("상품이 성공적으로 등록되었습니다.");
    }
}
