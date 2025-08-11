package com.example.hanaro.service;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
public class FileService {

    private final String uploadDir;

    public FileService() {
        Path projectRootPath = Paths.get(System.getProperty("user.dir"));
        this.uploadDir = projectRootPath.resolve(Paths.get("src", "main", "resources", "static", "upload")).toString();
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
        String thumbnailFilename = "s_" + savedFilename;
        String thumbnailPath = Paths.get(uploadPath.getPath(), thumbnailFilename).toString();

        Thumbnails.of(new File(savedPath))
                .size(100, 100)
                .toFile(new File(thumbnailPath));

        log.info("썸네일 파일이 생성되었습니다. 경로: {}", thumbnailPath);

        // DB에 저장하고, URL로 사용할 상대 경로 반환
        return "upload/" + datePath + "/" + savedFilename;
    }

    public void deleteFile(String relativePath) {
        if (relativePath == null || relativePath.isEmpty()) {
            return;
        }

        try {
            // 원본 파일 경로
            String fullPath = new File(uploadDir, relativePath.replace("upload/", "")).getCanonicalPath();
            File originalFile = new File(fullPath);

            if (originalFile.exists()) {
                File parentDir = originalFile.getParentFile();
                String filename = originalFile.getName();
                File thumbnailFile = new File(parentDir, "s_" + filename);

                // 썸네일 파일 삭제
                if (thumbnailFile.exists()) {
                    if (thumbnailFile.delete()) {
                        log.info("썸네일 파일 삭제 성공: {}", thumbnailFile.getPath());
                    }
                }

                // 원본 파일 삭제
                if (originalFile.delete()) {
                    log.info("원본 파일 삭제 성공: {}", fullPath);
                }
            }
        } catch (IOException e) {
            log.error("파일 경로 변환 중 오류 발생: {}", relativePath, e);
        }
    }
}