package com.example.hanaro.service;

import com.example.hanaro.dto.*;
import com.example.hanaro.entity.Product;
import com.example.hanaro.exception.CustomException;
import com.example.hanaro.enums.ErrorCode;
import com.example.hanaro.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class ProductService {

    private final ProductRepository productRepository;
    private final FileService fileService;

    public void createProduct(ProductCreateRequestDto requestDto, MultipartFile imageFile) {
        try {
            String savedImagePath = fileService.storeFile(imageFile);

            Product product = Product.builder()
                    .name(requestDto.name())
                    .price(requestDto.price())
                    .stock(requestDto.stock())
                    .imageUrl(savedImagePath)
                    .build();

            productRepository.save(product);

        } catch (IOException e) {
            log.error("파일 저장 중 오류 발생", e);
            throw new CustomException(ErrorCode.FILE_UPLOAD_ERROR);
        }
        log.info("새로운 상품이 등록되었습니다. 상품명: {}, 가격: {}, 재고: {}", requestDto.name(), requestDto.price(), requestDto.stock());
    }
    public void updateProductStock(Long productId, ProductStockUpdateRequestDto requestDto) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
        product.updateStock(requestDto.stock());
        productRepository.save(product);
        log.info("상품 재고가 수정되었습니다. 상품 ID: {}, 새로운 재고: {}", productId, requestDto.stock());
    }
    public List<ProductResponseDto> getProducts(String keyword) {
        List<Product> products;
        if (keyword == null || keyword.isBlank()) {
            products = productRepository.findAll();
        } else {
            products = productRepository.findByNameContaining(keyword);
        }
        log.info("상품 목록 조회 완료. 검색어: {}, 상품 개수: {}", keyword, products.size());
        return products.stream()
                .map(ProductResponseDto::new)
                .collect(Collectors.toList());
    }

    public ProductDetailResponseDto getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
        log.info("상품 상세 조회 완료. 상품 ID: {}", productId);
        return new ProductDetailResponseDto(product);
    }

    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
        // 이미지 파일 삭제
        if (product.getImageUrl() != null) {
            fileService.deleteFile(product.getImageUrl());
        }
        productRepository.delete(product);
        log.info("상품이 삭제되었습니다. 상품 ID: {}", productId);
    }

    public void updateProduct(Long productId, ProductUpdateRequestDto requestDto, MultipartFile imageFile) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        String oldImagePath = product.getImageUrl();
        String newImagePath = oldImagePath;

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                newImagePath = fileService.storeFile(imageFile);
            } catch (IOException e) {
                log.error("파일 저장 중 오류 발생", e);
                throw new CustomException(ErrorCode.FILE_UPLOAD_ERROR);
            }
        }

        product.updateDetails(
                requestDto.name(),
                requestDto.price(),
                requestDto.stock(),
                newImagePath // 새 이미지가 있으면 새 경로, 없으면 기존 경로가 전달됨
        );

        if (oldImagePath != null && !oldImagePath.equals(newImagePath)) {
            fileService.deleteFile(oldImagePath);
        }
        productRepository.save(product);
        log.info("상품 정보가 업데이트되었습니다. 상품 ID: {}", productId);
    }
}
