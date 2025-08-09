package com.example.hanaro.service;

import com.example.hanaro.dto.ProductCreateRequestDto;
import com.example.hanaro.dto.ProductDetailResponseDto;
import com.example.hanaro.dto.ProductResponseDto;
import com.example.hanaro.dto.ProductStockUpdateRequestDto;
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
    }
    public void updateProductStock(Long productId, ProductStockUpdateRequestDto requestDto) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
        product.updateStock(requestDto.stock());
        productRepository.save(product);
    }
    public List<ProductResponseDto> getProducts(String keyword) {
        List<Product> products;
        if (keyword == null || keyword.isBlank()) {
            products = productRepository.findAll();
        } else {
            products = productRepository.findByNameContaining(keyword);
        }
        return products.stream()
                .map(ProductResponseDto::new)
                .collect(Collectors.toList());
    }

    public ProductDetailResponseDto getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
        return new ProductDetailResponseDto(product);
    }

    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
        productRepository.delete(product);
    }
}
