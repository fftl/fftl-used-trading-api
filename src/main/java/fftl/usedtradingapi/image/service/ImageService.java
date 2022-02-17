package fftl.usedtradingapi.image.service;

import fftl.usedtradingapi.commons.utils.S3Uploader;
import fftl.usedtradingapi.image.domain.Image;
import fftl.usedtradingapi.image.domain.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ImageService {

    private final S3Uploader s3Uploader;
    private final ImageRepository imageRepository;

    public boolean uploadProductImage(Long productId, List<MultipartFile> files){
        return true;
    }

    public boolean uploadUserImage(Long productId, MultipartFile file){
        return true;
    }

    public boolean deleteImage(Long imageId){
        return true;
    }
}
