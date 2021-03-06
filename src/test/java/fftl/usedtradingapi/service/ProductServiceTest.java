package fftl.usedtradingapi.service;

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
import fftl.usedtradingapi.product.service.ProductService;
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

    /**
     * Mock Data Create
     * */
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

        category = Category.builder().id(3L).categoryName("??????/????????????").build();

        categories = new ArrayList<>();
        categories.add(Category.builder().id(1L).categoryName("???????????????").build());
        categories.add(Category.builder().id(2L).categoryName("????????????").build());

        categoryIds = new ArrayList<>();
        categoryIds.add(1L);
        categoryIds.add(2L);

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

        reviews = Arrays.asList(
            Review.builder().product(product).user(user).id(1L).content("?????????!").build(),
            Review.builder().product(product).user(user).id(2L).content("????????????!").build(),
            Review.builder().product(product).user(user).id(3L).content("????????????!!").build()
        );

        user = User.builder()
            .id(1L)
            .username("fftl")
            .password("1234")
            .address(Address.builder().state("?????????").city("?????????").town("??????").build())
            .categories(categories)
            .build();

        product = Product.builder()
            .id(1L)
            .title("?????????")
            .description("????????? ????????? ?????????.")
            .category(Category.builder().id(1L).categoryName("???????????????").build())
            .price("120000")
            .status(Status.SALE)
            .address(Address.builder().state("?????????").city("?????????").town("??????").build())
            .user(user)
            .like(0)
            .review(reviews)
            .build();

        saveProductRequest = SaveProductRequest.builder()
            .title("?????????")
            .categoryId(1L)
            .price("120000")
            .description("????????? ????????? ?????????.")
            .status(Status.SALE)
            .userId(1L)
            .state("?????????")
            .city("?????????")
            .town("??????")
            .files(multipartFiles)
            .build();

        products = Arrays.asList(
            product,
            Product.builder().id(2L).title("????????? ?????????").description("?????? ????????? ????????? ???????????????.").category(Category.builder().id(1L).categoryName("???????????????").build()).price("40000").status(Status.SALE).address(Address.builder().state("?????????").city("?????????").town("??????").build()).user(user).status(Status.CANCEL).build(),
            Product.builder().id(3L).title("?????????").description("????????? ????????? ?????????").category(Category.builder().id(1L).categoryName("???????????????").build()).price("150000").status(Status.SALE).address(Address.builder().state("?????????").city("?????????").town("??????").build()).user(user).status(Status.SALE).build()
        );
    }

    @DisplayName("?????? ???????????? ?????????")
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

    @DisplayName("?????? ???????????? ?????????")
    @Test
    void updateProduct() throws Exception{
        //given
        when(categoryRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(category));
        when(productRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(product));
        SaveProductRequest updateProductRequest = SaveProductRequest.builder()
            .title("?????? ????????? ?????????")
            .categoryId(1L)
            .price("60000")
            .description("3?????? ??????????????????.")
            .status(Status.SALE)
            .userId(1L)
            .state("?????????")
            .city("?????????")
            .town("??????")
            .files(multipartFiles)
            .build();

        //when
        Product result = productService.updateProduct(productId, updateProductRequest);

        //then
        assertEquals(result.getPrice(), "60000");
        assertEquals(result.getTitle(), "?????? ????????? ?????????");
        assertEquals(result.getDescription(), "3?????? ??????????????????.");

    }
    @DisplayName("?????? ?????? ??????????????? ???????????? ?????????")
    @Test
    void completeProduct() {
        //given
        when(productRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(product));

        //when
        productService.completeProduct(productId);

        //then
        assertEquals(product.getStatus(), Status.COMPLETE);
    }

    @DisplayName("?????? ?????? ??????????????? ???????????? ?????????")
    @Test
    void cancelProduct() {
        //given
        when(productRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(product));

        //when
        productService.cancelProduct(productId);

        //then
        assertEquals(product.getStatus(), Status.CANCEL);
    }

    @DisplayName("?????? ?????? ??????????????? ???????????? ?????????")
    @Test
    void saleProduct() {
        //given
        product = Product.builder()
            .id(1L)
            .title("?????????")
            .description("????????? ????????? ?????????.")
            .category(Category.builder().id(1L).categoryName("???????????????").build())
            .price("120000")
            .status(Status.CANCEL)
            .address(Address.builder().state("?????????").city("?????????").town("??????").build())
            .user(user)
            .build();
        when(productRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(product));

        //when
        productService.saleProduct(productId);

        //then
        assertEquals(product.getStatus(), Status.SALE);
    }

    @DisplayName("?????? ????????? ???????????? ?????????")
    @Test
    void plusLike() {
        //given
        when(productRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(product));

        //when
        productService.plusLike(productId);

        //then
        assertEquals(product.getLike(), 1);
    }

    @DisplayName("?????? ????????? ???????????? ?????????")
    @Test
    void minusLike() {
        //given
        when(productRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(product));

        //when
        productService.minusLike(productId);

        //then
        assertEquals(product.getLike(), -1);
    }

    @DisplayName("?????? ?????? ?????? ???????????? ?????????")
    @Test
    void getAllReviewProduct() {
        //given
        when(productRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(product));

        //when
        List<Review> result = productService.getAllReviewProduct(productId);

        //then
        assertEquals(result.size(), 3);
        assertEquals(result.get(0).getContent(), "?????????!");
        assertEquals(result.get(1).getContent(), "????????????!");

    }

    @DisplayName("?????? ????????? ???????????? ?????????")
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

    @DisplayName("?????? ????????? ???????????? ?????????")
    @Test
    void deleteProductImage() {
        //given
        product = Product.builder()
            .id(1L)
            .title("?????????")
            .description("????????? ????????? ?????????.")
            .category(Category.builder().id(1L).categoryName("???????????????").build())
            .price("120000")
            .status(Status.SALE)
            .address(Address.builder().state("?????????").city("?????????").town("??????").build())
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

    @DisplayName("?????? ??????????????? ????????? ????????? ???????????? ?????????")
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