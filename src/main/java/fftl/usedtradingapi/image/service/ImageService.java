package fftl.usedtradingapi.image.service;

import fftl.usedtradingapi.commons.utils.S3Uploader;
import fftl.usedtradingapi.image.domain.Image;
import fftl.usedtradingapi.image.domain.ImageRepository;
import fftl.usedtradingapi.image.domain.ImageType;
import fftl.usedtradingapi.product.domain.Product;
import fftl.usedtradingapi.product.service.ProductService;
import fftl.usedtradingapi.user.domain.User;
import fftl.usedtradingapi.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ImageService {

    private final S3Uploader s3Uploader;
    private final ImageRepository imageRepository;
    private final ProductService productService;
    private final UserService userService;

    public List<Image> uploadProductImage(List<MultipartFile> files, Long productId) throws IOException {
        List<Image> images = new ArrayList<>();
        List<String> imageUrls = s3Uploader.uploadProductImage(files);

        Product product = productService.getOneProduct(productId);

        for(String imageUrl : imageUrls){
            images.add(imageRepository.save(Image.builder()
                .url(imageUrl)
                .imageType(ImageType.Product)
                .product(product)
                .build()));
        }
        return images;
    }

    public Image uploadUserImage(MultipartFile file, Long userId) throws IOException{
        String imageUrl = s3Uploader.uploadUserImage(file);
        User user = userService.getOneUser(userId);

        return imageRepository.save(Image.builder()
            .url(imageUrl)
            .imageType(ImageType.User)
            .user(user)
            .build());
    }

    public void deleteImage(Long imageId){
        Image image = imageRepository.findById(imageId).orElseThrow(() -> new RuntimeException("해당 아이디를 가진 이미지가 존재하지 않습니다."));
        imageRepository.delete(image);
    }

}
