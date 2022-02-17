package fftl.usedtradingapi.image.controller;

import fftl.usedtradingapi.commons.utils.S3Uploader;
import fftl.usedtradingapi.image.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequestMapping("/image")
@RestController
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    //한개 이상의 여러개의 파일을 올릴 수 있기 때문에 List로 받습니다.
    @PostMapping("/product/{productId}")
    public boolean uploadProductImage(@RequestParam("image") List<MultipartFile> multipartFile, @PathVariable Long productId) throws IOException {
        return true;
    }

    @PostMapping("/user/{userId}")
    public boolean uploadUserImage(@RequestParam("image") MultipartFile multipartFile, @PathVariable Long productId) throws IOException {
        return true;
    }

    @DeleteMapping("/{imageId}")
    public boolean deleteImage(@PathVariable Long imageId){
        return true;
    }
}
