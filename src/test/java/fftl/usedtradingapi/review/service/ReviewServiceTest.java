package fftl.usedtradingapi.review.service;

import fftl.usedtradingapi.commons.domain.Category;
import fftl.usedtradingapi.product.domain.Address;
import fftl.usedtradingapi.product.domain.Product;
import fftl.usedtradingapi.product.domain.Status;
import fftl.usedtradingapi.product.service.ProductService;
import fftl.usedtradingapi.review.domain.Review;
import fftl.usedtradingapi.review.domain.ReviewRepository;
import fftl.usedtradingapi.review.dto.SaveReviewRequest;
import fftl.usedtradingapi.user.domain.User;
import fftl.usedtradingapi.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserService userService;

    @Mock
    private ProductService productService;

    private Long userId;
    private User user;

    private Long productId;
    private List<Product> products;
    private Product product;

    private Long reviewId;
    private List<Review> reviews;
    private Review review;
    private SaveReviewRequest saveReviewRequest;

    private Long categoryId;
    private Category category;
    private List<Category> categories;

    @BeforeEach
    void setUp(){
        userId = 1L;
        productId = 1L;
        reviewId = 1L;
        categoryId = 1L;

        category = Category.builder().id(3L).categoryName("가구/인테리어").build();

        categories = new ArrayList<>();
        categories.add(Category.builder().id(1L).categoryName("디지털기기").build());
        categories.add(Category.builder().id(2L).categoryName("생활가전").build());

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

        products = Arrays.asList(
            product,
            Product.builder().id(2L).title("키보드 마우스").description("무선 키보드 마우스 세트입니다.").category(Category.builder().id(1L).categoryName("디지털기기").build()).price("40000").status(Status.SALE).address(Address.builder().state("경기도").city("부천시").town("상동").build()).user(user).status(Status.CANCEL).build(),
            Product.builder().id(3L).title("에어팟").description("에어팟 미개봉 입니다").category(Category.builder().id(1L).categoryName("디지털기기").build()).price("150000").status(Status.SALE).address(Address.builder().state("경기도").city("부천시").town("상동").build()).user(user).status(Status.SALE).build()
        );

        user = User.builder().id(1L).username("fftl").password("password")
            .address(Address.builder().state("경기도").city("부천시").town("상동").build())
            .categories(categories).myProducts(products)
            .build();

        review = Review.builder().product(product).user(user).id(1L).content("잘 작동하네요!").build();

        reviews = new ArrayList<>(Arrays.asList(
            Review.builder().product(product).user(user).id(2L).content("맛있어요!").build(),
            Review.builder().product(product).user(user).id(3L).content("재밌어요!!").build(),
            Review.builder().product(product).user(user).id(4L).content("좋아요!").build()
        ));

        saveReviewRequest = SaveReviewRequest
            .builder()
            .userId(userId)
            .productId(productId)
            .content("잘산거 같아요!!")
            .build();

    }

    @Test
    void saveReview() {
        //given
        when(userService.getOneUser(any(Long.class))).thenReturn(user);
        when(productService.getOneProduct(any(Long.class))).thenReturn(product);
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        //when
        Review result = reviewService.saveReview(saveReviewRequest);

        //then
        assertEquals(result.getContent(), "잘 작동하네요!");
        assertEquals(result.getUser(), user);
    }

    @Test
    void updateReview() {
        //given
        when(reviewRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(review));

        //when
        Review result = reviewService.updateReview(reviewId, "변경이 되었을까요?");

        //then
        assertEquals(result.getContent(), "변경이 되었을까요?");
        assertEquals(result.getUser(), user);
    }

    @Test
    void deleteReview() {
        //기능이 void를 반환하기 때문에 테스트를 생략합니다.
    }
}