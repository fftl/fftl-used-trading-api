package fftl.usedtradingapi.controller;

import fftl.usedtradingapi.commons.domain.Category;
import fftl.usedtradingapi.image.domain.Image;
import fftl.usedtradingapi.image.domain.ImageType;
import fftl.usedtradingapi.image.dto.ImageResponse;
import fftl.usedtradingapi.product.domain.Address;
import fftl.usedtradingapi.product.domain.Product;
import fftl.usedtradingapi.product.domain.Status;
import fftl.usedtradingapi.review.controller.ReviewController;
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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.*;
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

    Product product;
    List<Product> products;

    Category category;
    List<Category> categories;

    List<Image> images;

    Review review;
    List<Review> reviews;

    @BeforeEach
    void setUp() {

        category = Category.builder().id(1L).categoryName("?????????/??????").build();

        categories = new ArrayList<>();
        categories.add(Category.builder().id(1L).categoryName("?????????/??????").build());
        categories.add(Category.builder().id(2L).categoryName("??????").build());

        user = User.builder()
            .id(1L)
            .username("fftl")
            .password("123123")
            .address(Address.builder().state("?????????").city("?????????").town("??????").build())
            .categories(categories)
            .removed(false)
            .build();

        product = Product.builder()
            .id(1L)
            .title("????????? ??????????????????.")
            .description("????????? ????????? ?????? ?????????~")
            .price("80000")
            .address(Address.builder().state("?????????").city("?????????").town("??????").build())
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
            .title("????????? ?????????")
            .description("?????? ????????? ?????????.")
            .price("20000")
            .address(Address.builder().state("?????????").city("?????????").town("??????").build())
            .user(user)
            .category(category)
            .like(0)
            .status(Status.SALE)
            .build());

        review = Review.builder()
            .id(1L)
            .content("?????? ????????? ??? ?????????. ?????? ?????? ???????????????!")
            .product(product)
            .user(user)
            .build();
        reviews = new ArrayList<>();
        reviews.add(Review.builder()
            .id(1L)
            .content("?????????!")
            .product(product)
            .user(user)
            .build());
    }

    @DisplayName("?????? ???????????? ?????????")
    @Test
    void saveReview() throws Exception{
        //given
        when(reviewService.saveReview(any(SaveReviewRequest.class))).thenReturn(review);
        JSONObject jsonObject = new JSONObject()
            .put("content", "?????? ????????? ??? ?????????. ?????? ?????? ???????????????!")
            .put("userId", 1L)
            .put("productId",1L);

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
                        fieldWithPath("content").type(JsonFieldType.STRING).description("?????? ??????"),
                        fieldWithPath("userId").type(JsonFieldType.NUMBER).description("????????? key"),
                        fieldWithPath("productId").type(JsonFieldType.NUMBER).description("?????? ?????? ?????? key")
                    ),
                    responseFields(
                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("?????? ????????????"),
                        fieldWithPath("message").type(JsonFieldType.STRING).type(JsonFieldType.NULL).description("?????? ????????? ?????????"),
                        fieldWithPath("data").type(JsonFieldType.VARIES).description("?????? ?????????(?????? ??? ?????? ??????)"),

                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("????????? ?????? key"),
                        fieldWithPath("data.content").type(JsonFieldType.STRING).description("????????? ?????? ??????"),
                        fieldWithPath("data.userId").type(JsonFieldType.NUMBER).description("?????? ????????? key"),
                        fieldWithPath("data.productId").type(JsonFieldType.NUMBER).description("?????? ?????? ?????? key")
                )));
    }

    @DisplayName("?????? ???????????? ?????????")
    @Test
    void updateReview() throws Exception{
        //given
        when(reviewService.updateReview(any(Long.class),any(String.class))).thenReturn(review);

        //when
        ResultActions actions = mockMvc.perform(
            RestDocumentationRequestBuilders.patch("/review/{reviewId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .param("content", "????????? ???????????????!!"));
        //then
        actions
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").doesNotExist())
            .andDo(document("updateReview",
                    pathParameters(
                      parameterWithName("reviewId").description("?????? key")
                    ),
                    requestParameters(
                        parameterWithName("content").description("?????? ?????? ??????")
                    ),
                    responseFields(
                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("?????? ????????????"),
                        fieldWithPath("message").type(JsonFieldType.STRING).type(JsonFieldType.NULL).description("?????? ????????? ?????????"),
                        fieldWithPath("data").type(JsonFieldType.VARIES).description("?????? ?????????(?????? ??? ?????? ??????)"),

                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("????????? ?????? key"),
                        fieldWithPath("data.content").type(JsonFieldType.STRING).description("????????? ?????? ??????"),
                        fieldWithPath("data.userId").type(JsonFieldType.NUMBER).description("?????? ????????? key"),
                        fieldWithPath("data.productId").type(JsonFieldType.NUMBER).description("?????? ?????? ?????? key")
                )));
    }

    @DisplayName("?????? ???????????? ?????????")
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
            .andDo(document("deleteReview",
                    pathParameters(
                      parameterWithName("reviewId").description("????????? ?????? key")
                    ),
                    responseFields(
                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("?????? ????????????"),
                        fieldWithPath("message").type(JsonFieldType.STRING).type(JsonFieldType.NULL).description("?????? ????????? ?????????"),
                        fieldWithPath("data").type(JsonFieldType.VARIES).description("?????? ?????????")
                )));

    }
}