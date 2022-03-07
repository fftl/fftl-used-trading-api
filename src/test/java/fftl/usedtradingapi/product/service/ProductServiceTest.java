package fftl.usedtradingapi.product.service;

import fftl.usedtradingapi.commons.domain.Category;
import fftl.usedtradingapi.commons.domain.CategoryRepository;
import fftl.usedtradingapi.image.domain.Image;
import fftl.usedtradingapi.image.domain.ImageType;
import fftl.usedtradingapi.image.service.ImageService;
import fftl.usedtradingapi.product.domain.Address;
import fftl.usedtradingapi.product.domain.Product;
import fftl.usedtradingapi.product.domain.ProductRepository;
import fftl.usedtradingapi.product.domain.Status;
import fftl.usedtradingapi.product.dto.SaveProductRequest;
import fftl.usedtradingapi.user.domain.User;
import fftl.usedtradingapi.user.domain.UserRepository;
import fftl.usedtradingapi.user.dto.LoginUserRequest;
import fftl.usedtradingapi.user.dto.SaveUserRequest;
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
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ImageService imageService;

    private User user;

    private Category category;
    private List<Category> categories;
    private List<Long> categoryIds;

    private Product product;
    private SaveProductRequest saveProductRequest;

    private Long userId;
    private Long productId;

    private Image image;
    private List<Image> images;

    private MultipartFile multipartFile;
    private List<MultipartFile> multipartFiles;

    @BeforeEach
    void setUp() throws Exception{

        userId = 1L;
        productId = 1L;

        category = Category.builder().id(3L).categoryName("가구/인테리어").build();

        categories = new ArrayList<>();
        categories.add(Category.builder().id(1L).categoryName("디지털기기").build());
        categories.add(Category.builder().id(2L).categoryName("생활가전").build());

        categoryIds = new ArrayList<>();
        categoryIds.add(1L);
        categoryIds.add(2L);

        multipartFile = new MockMultipartFile("test.png", new FileInputStream(new File("src/main/resources/뚱이.jpg")));
        multipartFiles = Arrays.asList(
            multipartFile,
            new MockMultipartFile("test22.png", new FileInputStream(new File("src/main/resources/뚱이.jpg")))
        );

        image = Image.builder().id(1L).imageType(ImageType.User).user(user).url("test.png").build();
        images = Arrays.asList(
            image,
            Image.builder().id(2L).imageType(ImageType.User).user(user).url("test222.png").build()
        );

        user = User.builder()
            .id(1L)
            .username("fftl")
            .password("1234")
            .address(Address.builder().state("경기도").city("부천시").town("상동").build())
            .categories(categories)
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
            .build();

        saveProductRequest = SaveProductRequest.builder()
            .title("모니터")
            .categoryId(1L)
            .price("120000")
            .description("미개봉 모니터 입니당.")
            .status(Status.SALE)
            .userId(1L)
            .state("경기도")
            .city("부천시")
            .town("상동")
            .files(multipartFiles)
            .build();

    }

    @DisplayName("상품 등록하기 테스트")
    @Test
    void saveProduct() throws Exception{
        //given
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(user));
        when(categoryRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(imageService.uploadProductImage(multipartFiles, userId)).thenReturn(images);

        //when
        Product result = productService.saveProduct(saveProductRequest);

        //then
        assertEquals(result.getPrice(), product.getPrice());
        assertEquals(result.getImages(), images);
    }


    @Test
    void updateProduct() {
    }

    @Test
    void getAllProduct() {
    }

    @Test
    void getOneProduct() {
    }

    @Test
    void getProductByState() {
    }

    @Test
    void getProductByCity() {
    }

    @Test
    void getProductByTown() {
    }

    @Test
    void saleProduct() {
    }

    @Test
    void completeProduct() {
    }

    @Test
    void cancelProduct() {
    }

    @Test
    void plusLike() {
    }

    @Test
    void minusLike() {
    }

    @Test
    void getAllReviewProduct() {
    }

    @Test
    void addProductImage() {
    }

    @Test
    void deleteProductImage() {
    }

    @Test
    void findSaleStatusProducts() {
    }
}