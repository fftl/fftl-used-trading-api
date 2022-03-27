package fftl.usedtradingapi.controller;

import fftl.usedtradingapi.commons.domain.Category;
import fftl.usedtradingapi.image.domain.Image;
import fftl.usedtradingapi.image.domain.ImageType;
import fftl.usedtradingapi.product.controller.ProductController;
import fftl.usedtradingapi.product.domain.Address;
import fftl.usedtradingapi.product.domain.Product;
import fftl.usedtradingapi.product.domain.Status;
import fftl.usedtradingapi.product.dto.SaveProductRequest;
import fftl.usedtradingapi.product.service.ProductService;
import fftl.usedtradingapi.user.domain.User;
import fftl.usedtradingapi.user.dto.SaveUserRequest;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@AutoConfigureRestDocs
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    User user;
    Product product;

    List<Category> categories;
    Category category;

    List<Image> images;

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

    }

    @Test
    void saveProduct() throws Exception {

        when(productService.saveProduct(any(SaveProductRequest.class))).thenReturn(product);
//        List<MockMultipartFile> files = new ArrayList<>();
//        files.add(new MockMultipartFile("img", "img.png".getBytes()));

        JSONArray jsonFiles = new JSONArray();
        jsonFiles.put(new MockMultipartFile("img", "img.png".getBytes()));

        JSONObject jsonObject = new JSONObject()
            .put("title", "미개봉 모니터입니다.")
            .put("categoryId", 1L)
            .put("price", "80000")
            .put("description", "미개봉 모니터 싸게 팔아요~")
            .put("status", "SALE")
            .put("userId", 1L)
            .put("state", "경기도")
            .put("city","부천시")
            .put("town", "상동")
            .put("files", jsonFiles);

        //when
        ResultActions actions = mockMvc.perform(
            post("/product")
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
            .andDo(document("saveProduct",
                    requestFields(
                        fieldWithPath("title").type(JsonFieldType.STRING).description("상품 이름"),
                        fieldWithPath("categoryId").type(JsonFieldType.STRING).description("카테고리 key"),
                        fieldWithPath("price").type(JsonFieldType.ARRAY).description("가격"),
                        fieldWithPath("description").type(JsonFieldType.STRING).description("상품 설명"),
                        fieldWithPath("status").type(JsonFieldType.STRING).description("상품 상태"),
                        fieldWithPath("userId").type(JsonFieldType.STRING).description("등록자 key"),
                        fieldWithPath("state").type(JsonFieldType.STRING).description("거주지(시도)"),
                        fieldWithPath("city").type(JsonFieldType.STRING).description("거주지(시군구)"),
                        fieldWithPath("town").type(JsonFieldType.STRING).description("거주지(읍면동)"),
                        fieldWithPath("files").type(JsonFieldType.VARIES).description("상품 이미지들").optional()
                    ),
                    responseFields(
                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공여부"),
                        fieldWithPath("message").type(JsonFieldType.STRING).type(JsonFieldType.NULL).description("요청 실패시 메시지"),
                        fieldWithPath("data").type(JsonFieldType.VARIES).description("요청 데이터(등록 된 상품 정보)"),

                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("상품 key"),
                        fieldWithPath("data.title").type(JsonFieldType.STRING).description("상품 이름"),
                        fieldWithPath("data.category").type(JsonFieldType.VARIES).description("상품 카테고리"),
                        fieldWithPath("data.category.id").type(JsonFieldType.VARIES).description("카테고리 아이디"),
                        fieldWithPath("data.category.categoryName").type(JsonFieldType.VARIES).description("카테고리 이름"),
                        fieldWithPath("data.price").type(JsonFieldType.VARIES).description("상품 가격"),
                        fieldWithPath("data.description").type(JsonFieldType.VARIES).description("상품 설명"),
                        fieldWithPath("data.status").type(JsonFieldType.VARIES).description("상품 상태"),
                        fieldWithPath("data.address").type(JsonFieldType.VARIES).description("거주지"),
                        fieldWithPath("data.address.state").type(JsonFieldType.VARIES).description("시도"),
                        fieldWithPath("data.address.city").type(JsonFieldType.VARIES).description("시군구"),
                        fieldWithPath("data.address.town").type(JsonFieldType.VARIES).description("음면동"),
                        fieldWithPath("data.like").type(JsonFieldType.VARIES).description("상품 관심도").optional(),
                        fieldWithPath("data.userId").type(JsonFieldType.VARIES).description("판매자 아이디").optional(),
                        fieldWithPath("data.images").type(JsonFieldType.VARIES).description("상품 이미지들").optional(),
                        fieldWithPath("data.reviews").type(JsonFieldType.STRING).description("거래 후기들").optional()
                    )
                ));

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
    void plusLike() {
    }

    @Test
    void minusLike() {
    }

    @Test
    void updateProduct() {
    }

    @Test
    void cancelProduct() {
    }

    @Test
    void completeProduct() {
    }

    @Test
    void saleProduct() {
    }

    @Test
    void addProductImage() {
    }

    @Test
    void deleteProductImage() {
    }

    @Test
    void getAllReviewProduct() {
    }
}