package fftl.usedtradingapi.commons.utils;

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
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3Uploader {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    public String bucket;

    public String upload(MultipartFile multipartFile, String dirName) throws IOException {
        File uploadFile = convert(multipartFile).orElseThrow(() -> new IllegalArgumentException("Error: 멀티파일 -> 파일 변환 실패"));
        return upload(uploadFile, dirName);
    }

    public String upload(File uploadFile, String dirName){
        String fileName = dirName + "/" + UUID.randomUUID() + uploadFile.getName();
        String uploadImageUrl = putS3(uploadFile, fileName);
        removeNewFile(uploadFile);
        return uploadImageUrl;
    }

    private String putS3(File uploadFile, String fileName){
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    private void removeNewFile(File targetFile){
        if(targetFile.delete()){
            log.info("파일 삭제 성공");
            return;
        }

        log.info("파일 삭제 실패");
    }

    private Optional<File> convert(MultipartFile file) throws IOException {
        System.out.println("convert1");
        File convertFile = new File(System.getProperty("user.dir") + "/" + file.getOriginalFilename());
        System.out.println(System.getProperty("user.dir") + "/" + file.getOriginalFilename());
        System.out.println("convert2");
        if(convertFile.createNewFile()) {
            System.out.println("convert3");
            try(FileOutputStream fos = new FileOutputStream(convertFile)){
                System.out.println("convert4");
                fos.write(file.getBytes());
                System.out.println("convert5");
            }
            System.out.println("convert6");
            return Optional.of(convertFile);
        }
        System.out.println("convert7");
        return Optional.empty();
    }
}
