package fftl.usedtradingapi.service;

import fftl.usedtradingapi.commons.domain.Category;
import fftl.usedtradingapi.commons.utils.S3Uploader;
import fftl.usedtradingapi.image.domain.Image;
import fftl.usedtradingapi.image.domain.ImageRepository;
import fftl.usedtradingapi.image.domain.ImageType;
import fftl.usedtradingapi.image.service.ImageService;
import fftl.usedtradingapi.product.domain.Address;
import fftl.usedtradingapi.product.domain.Product;
import fftl.usedtradingapi.product.domain.ProductRepository;
import fftl.usedtradingapi.product.domain.Status;
import fftl.usedtradingapi.user.domain.User;
import fftl.usedtradingapi.user.domain.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @InjectMocks
    private ImageService imageService;

    @Mock
    private S3Uploader s3Uploader;

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    private Long userId;
    private User user;

    private Long productId;
    private Product product;

    private Long imageId;
    private Image image;
    private List<Image> images;
    private List<String> imageUrls;

    private MultipartFile multipartFile;
    private List<MultipartFile> multipartFiles;

    @BeforeEach
    void setUp() throws Exception{
        userId = 1L;
        productId = 1L;
        imageId = 1L;

        multipartFile = new MockMultipartFile("test.png", new FileInputStream(new File("src/main/resources/testImage.jpg")));
        multipartFiles = Arrays.asList(
            multipartFile,
            new MockMultipartFile("test22.png", new FileInputStream(new File("src/main/resources/testImage.jpg")))
        );

        image = Image.builder().id(1L).imageType(ImageType.User).user(user).url("test.png").build();
        images = new ArrayList<>(Arrays.asList(
            image,
            Image.builder().id(2L).imageType(ImageType.User).user(user).url("test222.png").build()
        ));
        imageUrls = Arrays.asList(
            "test.png",
            "test222.png"
        );

        user = User.builder()
            .id(1L)
            .username("fftl")
            .password("1234")
            .address(Address.builder().state("경기도").city("부천시").town("상동").build())
            .build();

        product = Product.builder()
            .id(1L)
            .title("모니터")
            .description("미개봉 모니터 입니당.")
            .category(Category.builder().id(1L).categoryName("디지털기기").build())
            .price("120000")
            .status(Status.SALE)
            .address(Address.builder().state("경기도").city("부천시").town("상동").build())
            .user(user)
            .like(0)
            .build();
    }

    @DisplayName("유저 이미지 업로드 테스트")
    @Test
    void uploadUserImage() throws Exception{
        //given
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(user));
        when(s3Uploader.uploadUserImage(multipartFile)).thenReturn(image.getUrl());
        when(imageRepository.save(any(Image.class))).thenReturn(image);

        //when
        Image result = imageService.uploadUserImage(multipartFile, userId);

        //then
        assertEquals(result.getImageType(), ImageType.User);
        assertEquals(result.getUrl(), "test.png");
    }

    @DisplayName("상품 이미지 업로드 테스트")
    @Test
    void uploadProductImage() throws Exception{
        //given
        when(s3Uploader.uploadProductImage(any(List.class))).thenReturn(imageUrls);
        when(productRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(product));
        when(imageRepository.save(any(Image.class))).thenReturn(image);

        //when
        List<Image> result = imageService.uploadProductImage(multipartFiles, productId);

        //then
        assertEquals(result.size(), 2);
        assertEquals(result.get(0).getUrl(), "test.png");

    }

    @DisplayName("이미지 하나 가져오기 테스트")
    @Test
    void getOneImage() {
        //given
        when(imageRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(image));

        //when
        Image result = imageService.getOneImage(imageId);

        //then
        assertEquals(result.getUrl(), image.getUrl());

    }

    @DisplayName("이미지 삭제하기 테스트")
    @Test
    void deleteImage() {
        //기능이 void를 반환하기 때문에 테스트를 생략합니다.
    }
}