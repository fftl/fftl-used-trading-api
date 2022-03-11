package fftl.usedtradingapi.commons.utils;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3Uploader {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    public String bucket;

    public String uploadUserImage(MultipartFile file) throws IOException {

        if(file == null) {
            throw new RuntimeException("파일이 입력되지 않았습니다.");
        }

        return upload(file);
    }

    public List<String> uploadProductImage(List<MultipartFile> files) throws IOException {

        List<String> imgUrls = new ArrayList<>();

        if(files.size() < 1){
            throw new RuntimeException("상품 이미지는 하나 이상만 입력할 수 있습니다.");
        } else {
            for (MultipartFile file : files) {
                imgUrls.add(upload(file));
            }
        }

        return imgUrls;
    }

    public String upload(MultipartFile multipartFile) throws IOException {
        File uploadFile = convert(multipartFile).orElseThrow(() -> new IllegalArgumentException("Error: 멀티파일 -> 파일 변환 실패"));
        return upload(uploadFile);
    }

    public String upload(File uploadFile){
        String fileName = UUID.randomUUID() + uploadFile.getName();
        String uploadImageUrl = putS3(uploadFile, fileName);
        removeNewFile(uploadFile);
        return uploadImageUrl;
    }

    private String putS3(File uploadFile, String fileName){
        amazonS3.putObject(new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3.getUrl(bucket, fileName).toString();
    }

    private void removeNewFile(File targetFile){
        if(targetFile.delete()){
            log.info("파일 삭제 성공");
            return;
        }
        log.info("파일 삭제 실패");
    }

    private Optional<File> convert(MultipartFile file) throws IOException {
        File convertFile = new File(System.getProperty("user.dir") + "/" + file.getOriginalFilename());
        if(convertFile.createNewFile()) {
            try(FileOutputStream fos = new FileOutputStream(convertFile)){
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }
        return Optional.empty();
    }

}
