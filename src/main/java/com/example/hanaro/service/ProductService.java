package com.example.hanaro.service;

import com.example.hanaro.dto.ProductCreateRequestDto;
import com.example.hanaro.entity.Product;
import com.example.hanaro.exception.CustomException;
import com.example.hanaro.enums.ErrorCode;
import com.example.hanaro.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
}
