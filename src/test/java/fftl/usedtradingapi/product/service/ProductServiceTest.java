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
import fftl.usedtradingapi.review.domain.Review;
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
    private List<Product> products;
    private SaveProductRequest saveProductRequest;

    private Long userId;
    private Long productId;
    private Long imageId;

    private Image image;
    private List<Image> images;

    private MultipartFile multipartFile;
    private List<MultipartFile> multipartFiles;

    private List<Review> reviews;

    @BeforeEach
    void setUp() throws Exception{

        userId = 1L;
        productId = 1L;
        imageId = 1L;

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
        images = new ArrayList<>(Arrays.asList(
            image,
            Image.builder().id(2L).imageType(ImageType.User).user(user).url("test222.png").build()
        ));

        reviews = Arrays.asList(
            Review.builder().product(product).user(user).id(1L).content("좋아요!").build(),
            Review.builder().product(product).user(user).id(2L).content("맛있어요!").build(),
            Review.builder().product(product).user(user).id(3L).content("재밌어요!!").build()
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
            .like(0)
            .review(reviews)
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

        products = Arrays.asList(
            product,
            Product.builder().id(2L).title("키보드 마우스").description("무선 키보드 마우스 세트입니다.").category(Category.builder().id(1L).categoryName("디지털기기").build()).price("40000").status(Status.SALE).address(Address.builder().state("경기도").city("부천시").town("상동").build()).user(user).status(Status.CANCEL).build(),
            Product.builder().id(3L).title("에어팟").description("에어팟 미개봉 입니다").category(Category.builder().id(1L).categoryName("디지털기기").build()).price("150000").status(Status.SALE).address(Address.builder().state("경기도").city("부천시").town("상동").build()).user(user).status(Status.SALE).build()
        );
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

    @DisplayName("상품 수정하기 테스트")
    @Test
    void updateProduct() throws Exception{
        //given
        when(categoryRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(category));
        when(productRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(product));
        SaveProductRequest updateProductRequest = SaveProductRequest.builder()
            .title("조금 아쉬운 모니터")
            .categoryId(1L)
            .price("60000")
            .description("3개월 사용했습니당.")
            .status(Status.SALE)
            .userId(1L)
            .state("경기도")
            .city("부천시")
            .town("상동")
            .files(multipartFiles)
            .build();

        //when
        Product result = productService.updateProduct(productId, updateProductRequest);

        //then
        assertEquals(result.getPrice(), "60000");
        assertEquals(result.getTitle(), "조금 아쉬운 모니터");
        assertEquals(result.getDescription(), "3개월 사용했습니당.");

    }
    @DisplayName("상품 상태 판매완료로 변경하기 테스트")
    @Test
    void completeProduct() {
        //given
        when(productRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(product));

        //when
        productService.completeProduct(productId);

        //then
        assertEquals(product.getStatus(), Status.COMPLETE);
    }

    @DisplayName("상품 상태 판매취소로 변경하기 테스트")
    @Test
    void cancelProduct() {
        //given
        when(productRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(product));

        //when
        productService.cancelProduct(productId);

        //then
        assertEquals(product.getStatus(), Status.CANCEL);
    }

    @DisplayName("상품 상태 판매중으로 변경하기 테스트")
    @Test
    void saleProduct() {
        //given
        product = Product.builder()
            .id(1L)
            .title("모니터")
            .description("미개봉 모니터 입니당.")
            .category(Category.builder().id(1L).categoryName("디지털기기").build())
            .price("120000")
            .status(Status.CANCEL)
            .address(Address.builder().state("경기도").city("부천시").town("상동").build())
            .user(user)
            .build();
        when(productRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(product));

        //when
        productService.saleProduct(productId);

        //then
        assertEquals(product.getStatus(), Status.SALE);
    }

    @DisplayName("상품 좋아요 증가하기 테스트")
    @Test
    void plusLike() {
        //given
        when(productRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(product));

        //when
        productService.plusLike(productId);

        //then
        assertEquals(product.getLike(), 1);
    }

    @DisplayName("상품 좋아요 감소하기 테스트")
    @Test
    void minusLike() {
        //given
        when(productRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(product));

        //when
        productService.minusLike(productId);

        //then
        assertEquals(product.getLike(), -1);
    }

    @DisplayName("상품 모든 리뷰 가져오기 테스트")
    @Test
    void getAllReviewProduct() {
        //given
        when(productRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(product));

        //when
        List<Review> result = productService.getAllReviewProduct(productId);

        //then
        assertEquals(result.size(), 3);
        assertEquals(result.get(0).getContent(), "좋아요!");
        assertEquals(result.get(1).getContent(), "맛있어요!");

    }

    @DisplayName("상품 이미지 추가하기 테스트")
    @Test
    void addProductImage() throws Exception{
        //given
        when(productRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(product));
        when(imageService.uploadProductImage(any(List.class), any(Long.class))).thenReturn(images);

        //when
        Product result = productService.addProductImage(productId, multipartFiles);

        //then
        assertEquals(result.getImages().size(), 2);
        assertEquals(result.getImages().get(0).getUrl(), "test.png");
    }

    @DisplayName("상품 이미지 삭제하기 테스트")
    @Test
    void deleteProductImage() {
        //given
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
            .review(reviews)
            .images(images)
            .build();

        when(productRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(product));
        when(imageService.getOneImage(any(Long.class))).thenReturn(image);

        //when
        Product result = productService.deleteProductImage(productId, imageId);

        //then
        assertEquals(result.getImages().size(), 1);

    }

    @DisplayName("상품 리스트에서 판매중 상품만 가져오기 테스트")
    @Test
    void findSaleStatusProducts() {
        //when
        List<Product> result = productService.findSaleStatusProducts(products);

        //then
        assertEquals(result.size(), 2);
        assertEquals(result.get(0).getStatus(), Status.SALE);
        assertEquals(result.get(1).getStatus(), Status.SALE);
    }
}