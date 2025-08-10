package com.example.hanaro.repository;

import com.example.hanaro.entity.Product;
import com.example.hanaro.service.FileService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.annotation.Commit;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@SpringBootTest
public class ProductRepositoryTests {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private FileService fileService;

    @Test
    @DisplayName("초기 상품 데이터 10개 생성")
    @Transactional
    @Commit
    public void createInitialProducts() throws Exception {
        // 데이터가 이미 존재하면 중복 생성을 방지하기 위해 실행하지 않음
        if (productRepository.count() > 0) {
            System.out.println("데이터가 이미 존재하여 생성을 건너뜁니다.");
            return;
        }

        for (int i = 0; i < 10; i++) {
            ClassPathResource resource = new ClassPathResource("static/origin/sample_images_0" + i + ".png");
            InputStream inputStream = resource.getInputStream();
            MultipartFile multipartFile = new MockMultipartFile(
                    "sample_images.png", // 파일명
                    "sample_images_0" + i + ".png", // 원본 파일명
                    "image/png",         // 컨텐츠 타입
                    inputStream          // 파일 내용
            );
            String imageUrl = fileService.storeFile(multipartFile);

            Product product = Product.builder()
                    .name("샘플 상품 " + i)
                    .price(10000 + (i * 1000))
                    .stock(100)
                    .imageUrl(imageUrl)
                    .build();

            productRepository.save(product);
        }

        System.out.println("초기 제품 데이터 생성 완료. 총 " + productRepository.count() + "개");
    }
}
