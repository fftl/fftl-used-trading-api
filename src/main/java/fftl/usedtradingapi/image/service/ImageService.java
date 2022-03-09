package fftl.usedtradingapi.image.service;

import fftl.usedtradingapi.commons.utils.S3Uploader;
import fftl.usedtradingapi.image.domain.Image;
import fftl.usedtradingapi.image.domain.ImageRepository;
import fftl.usedtradingapi.image.domain.ImageType;
import fftl.usedtradingapi.product.domain.Product;
import fftl.usedtradingapi.product.domain.ProductRepository;
import fftl.usedtradingapi.user.domain.User;
import fftl.usedtradingapi.user.domain.UserRepository;
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
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    /**
     * 이미 생성되어 있는 user에 이미지를 추가합니다.
     * */
    public Image uploadUserImage(MultipartFile file, Long userId) throws IOException{
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("해당 아이디를 가진 유저는 존재하지 않습니다."));

        if(user.getImage() != null){
            throw new RuntimeException("유저는 하나의 이미지만 가질 수 있습니다.");
        }

        String imageUrl = s3Uploader.uploadUserImage(file);

        return imageRepository.save(Image.builder()
            .url(imageUrl)
            .imageType(ImageType.User)
            .user(user)
            .build());
    }

    /**
     * 이미 생성되어 있는 product에 이미지를 추가합니다.
     * */
    public List<Image> uploadProductImage(List<MultipartFile> files, Long productId) throws IOException {
        List<Image> images = new ArrayList<>();
        List<String> imageUrls = s3Uploader.uploadProductImage(files);

        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("해당 아이디를 가진 상품은 존재하지 않습니다."));

        for(String imageUrl : imageUrls){
            images.add(imageRepository.save(Image.builder()
                .url(imageUrl)
                .imageType(ImageType.Product)
                .product(product)
                .build()));
        }
        return images;
    }

    public Image getOneImage(Long imageId){
        Image image = imageRepository.findById(imageId).orElseThrow(() -> new RuntimeException("해당 아이디를 가진 이미지가 존재하지 않습니다."));
        return image;
    }

    /**
     * 등록되어 있는 이미지를 삭제합니다.
     * */
    public void deleteImage(Long imageId){
        Image image = imageRepository.findById(imageId).orElseThrow(() -> new RuntimeException("해당 아이디를 가진 이미지가 존재하지 않습니다."));
        imageRepository.delete(image);
    }

}
