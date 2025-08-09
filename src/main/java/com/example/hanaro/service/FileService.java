package com.example.hanaro.service;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
public class FileService {

    private final String uploadDir;

    public FileService() throws IOException {
        this.uploadDir = new ClassPathResource("static/upload").getFile().getAbsolutePath();
    }

    public String storeFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return null;
        }

        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        File uploadPath = new File(uploadDir, datePath);
        if (!uploadPath.exists()) {
            uploadPath.mkdirs();
        }
        String uuid = UUID.randomUUID().toString();
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String savedFilename = uuid + extension;
        String savedPath = Paths.get(uploadPath.getPath(), savedFilename).toString();

        // 원본 파일 저장
        file.transferTo(new File(savedPath));
        log.info("원본 파일이 저장되었습니다. 경로: {}", savedPath);

        // 썸네일 생성 및 저장
        // 원본 파일 앞에 "s_"를 붙여 썸네일 파일명
        String thumbnailFilename = "s_" + savedFilename;
        String thumbnailPath = Paths.get(uploadPath.getPath(), thumbnailFilename).toString();

        Thumbnails.of(new File(savedPath)) // 원본 파일
                .size(100, 100)           // 가로 100px, 세로 100px로 크기 조절
                .toFile(new File(thumbnailPath)); // 지정된 경로에 썸네일 파일로 저장

        log.info("썸네일 파일이 생성되었습니다. 경로: {}", thumbnailPath);

        return "upload/" + datePath + "/" + savedFilename;
    }
}