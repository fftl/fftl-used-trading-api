package fftl.usedtradingapi.review.controller;

import fftl.usedtradingapi.commons.domain.Category;
import fftl.usedtradingapi.image.domain.Image;
import fftl.usedtradingapi.image.domain.ImageType;
import fftl.usedtradingapi.image.dto.ImageResponse;
import fftl.usedtradingapi.product.domain.Address;
import fftl.usedtradingapi.product.domain.Product;
import fftl.usedtradingapi.product.domain.Status;
import fftl.usedtradingapi.review.domain.Review;
import fftl.usedtradingapi.review.dto.SaveReviewRequest;
import fftl.usedtradingapi.review.service.ReviewService;
import fftl.usedtradingapi.user.domain.User;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
@AutoConfigureRestDocs
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;

    User user;

    List<Product> products;
    Product product;

    List<Category> categories;
    Category category;

    List<Image> images;
    List<ImageResponse> imageResponses;

    Review review;
    List<Review> reviews;

    @BeforeEach
    void setUp(){

        categories = new ArrayList<>();
        categories.add(Category.builder().id(1L).categoryName("디지털/가전").build());
        categories.add(Category.builder().id(2L).categoryName("의류").build());

        category = Category.builder().id(1L).categoryName("디지털/가전").build();

        user = User.builder()
            .id(1L)
            .username("fftl")
            .password("123123")
            .address(Address.builder().state("경기도").city("부천시").town("상동").build())
            .categories(categories)
            .removed(false)
            .build();

        product = Product.builder()
            .id(1L)
            .title("미개봉 모니터입니다.")
            .description("미개봉 모니터 싸게 팔아요~")
            .price("80000")
            .address(Address.builder().state("경기도").city("부천시").town("상동").build())
            .user(user)
            .category(category)
            .like(0)
            .status(Status.SALE)
            .build();

        images = new ArrayList<>();
        images.add(Image.builder().id(1L).product(product).url("img.png").imageType(ImageType.Product).build());

        product.uploadProductImage(images);

        products = new ArrayList<>();
        products.add(product);
        products.add(Product.builder()
            .id(2L)
            .title("키보드 팔아요")
            .description("무선 키보드 입니다.")
            .price("20000")
            .address(Address.builder().state("경기도").city("부천시").town("상동").build())
            .user(user)
            .category(category)
            .like(0)
            .status(Status.SALE)
            .build());

        review = Review.builder()
            .id(1L)
            .content("잘 사용할게요!")
            .product(product)
            .user(user)
            .build();

        reviews = new ArrayList<>();
        reviews.add(Review.builder()
            .id(1L)
            .content("좋아요!")
            .product(product)
            .user(user)
            .build());

    }

    @DisplayName("상품 후기 작성 테스트")
    @Test
    void saveReview() throws Exception{
        //given
        when(reviewService.saveReview(any(SaveReviewRequest.class))).thenReturn(review);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("content", "잘 사용할게요!");
        jsonObject.put("userId", 1L);
        jsonObject.put("productId", 1L);

        //when
        ResultActions actions = mockMvc.perform(
            post("/review")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(String.valueOf(jsonObject)));

        //then
        actions
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").doesNotExist())
            .andDo(document("saveReview",
                    requestFields(
                        fieldWithPath("content").type(JsonFieldType.STRING).description("후기 내용"),
                        fieldWithPath("userId").type(JsonFieldType.NUMBER).description("등록자 key"),
                        fieldWithPath("productId").type(JsonFieldType.NUMBER).description("상품 key")
                    ),
                    responseFields(
                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공여부"),
                        fieldWithPath("message").type(JsonFieldType.STRING).type(JsonFieldType.NULL).description("요청 실패시 메시지"),
                        fieldWithPath("data").type(JsonFieldType.VARIES).description("요청 데이터(등록 된 상품 정보)"),

                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("리뷰 key"),
                        fieldWithPath("data.content").type(JsonFieldType.STRING).description(""),
                        fieldWithPath("data.userId").type(JsonFieldType.NUMBER).description("리뷰 작성자 key"),
                        fieldWithPath("data.productId").type(JsonFieldType.NUMBER).description("리뷰 상품 key")
                    )
                ));
    }

    @DisplayName("상품 후기 수정하기 테스트")
    @Test
    void updateReview() throws Exception{
        //given
        when(reviewService.updateReview(any(Long.class), any(String.class))).thenReturn(review);

        //when
        ResultActions actions = mockMvc.perform(
            RestDocumentationRequestBuilders.patch("/review/{reviewId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .param("content", "변경합니다."));

        //then
        actions
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").doesNotExist())
            .andDo(document("saveReview",
                    pathParameters(
                        parameterWithName("reviewId").description("리뷰 key")
                    ),
                    responseFields(
                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공여부"),
                        fieldWithPath("message").type(JsonFieldType.STRING).type(JsonFieldType.NULL).description("요청 실패시 메시지"),
                        fieldWithPath("data").type(JsonFieldType.VARIES).description("요청 데이터(등록 된 상품 정보)"),

                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("리뷰 key"),
                        fieldWithPath("data.content").type(JsonFieldType.STRING).description(""),
                        fieldWithPath("data.userId").type(JsonFieldType.NUMBER).description("리뷰 작성자 key"),
                        fieldWithPath("data.productId").type(JsonFieldType.NUMBER).description("리뷰 상품 key")
                    )
                ));
    }

    @DisplayName("상품 후기 삭제하기 테스트")
    @Test
    void deleteReview() throws Exception{
        //given
        //when
        ResultActions actions = mockMvc.perform(
            RestDocumentationRequestBuilders.delete("/review/{reviewId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8"));

        //then
        actions
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").doesNotExist())
            .andDo(document("saveReview",
                    pathParameters(
                        parameterWithName("reviewId").description("리뷰 key")
                    ),
                    responseFields(
                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공여부"),
                        fieldWithPath("message").type(JsonFieldType.STRING).type(JsonFieldType.NULL).description("요청 실패시 메시지"),
                        fieldWithPath("data").type(JsonFieldType.VARIES).type(JsonFieldType.NULL).description("데이터")
                    )
                ));
    }
}