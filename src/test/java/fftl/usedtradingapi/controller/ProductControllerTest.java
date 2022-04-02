package fftl.usedtradingapi.controller;

import fftl.usedtradingapi.commons.domain.Category;
import fftl.usedtradingapi.image.domain.Image;
import fftl.usedtradingapi.image.domain.ImageType;
import fftl.usedtradingapi.image.dto.ImageResponse;
import fftl.usedtradingapi.product.controller.ProductController;
import fftl.usedtradingapi.product.domain.Address;
import fftl.usedtradingapi.product.domain.Product;
import fftl.usedtradingapi.product.domain.Status;
import fftl.usedtradingapi.product.dto.SaveProductRequest;
import fftl.usedtradingapi.product.service.ProductService;
import fftl.usedtradingapi.review.domain.Review;
import fftl.usedtradingapi.user.domain.User;
import fftl.usedtradingapi.user.dto.SaveUserRequest;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
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

    List<Product> products;
    Product product;

    List<Category> categories;
    Category category;

    List<Image> images;
    List<ImageResponse> imageResponses;

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

        reviews = new ArrayList<>();
        reviews.add(Review.builder()
            .id(1L)
            .content("좋아요!")
            .product(product)
            .user(user)
            .build());

    }

    @DisplayName("상품 등록하기 테스트")
    @Test
    void saveProduct() throws Exception {

        when(productService.saveProduct(any(SaveProductRequest.class))).thenReturn(product);
        List<MockMultipartFile> files = new ArrayList<>();
        files.add(new MockMultipartFile("img", "img.png".getBytes()));

        JSONObject jsonObject = new JSONObject()
            .put("title", "미개봉 모니터입니다.")
            .put("categoryId", 1L)
            .put("price", "80000")
            .put("description", "미개봉 모니터 싸게 팔아요~")
            .put("status", "SALE")
            .put("userId", 1L)
            .put("state", "경기도")
            .put("city","부천시")
            .put("town", "상동");

        //when
        ResultActions actions = mockMvc.perform(
            multipart("/product")
                .file("files", files.get(0).getBytes())
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
                        fieldWithPath("categoryId").type(JsonFieldType.NUMBER).description("카테고리 key"),
                        fieldWithPath("price").type(JsonFieldType.STRING).description("가격"),
                        fieldWithPath("description").type(JsonFieldType.STRING).description("상품 설명"),
                        fieldWithPath("status").type(JsonFieldType.STRING).description("상품 상태"),
                        fieldWithPath("userId").type(JsonFieldType.NUMBER).description("등록자 key"),
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
                        fieldWithPath("data.category.id").type(JsonFieldType.NUMBER).description("카테고리 아이디"),
                        fieldWithPath("data.category.categoryName").type(JsonFieldType.STRING).description("카테고리 이름"),
                        fieldWithPath("data.price").type(JsonFieldType.STRING).description("상품 가격"),
                        fieldWithPath("data.description").type(JsonFieldType.STRING).description("상품 설명"),
                        fieldWithPath("data.status").type(JsonFieldType.STRING).description("상품 상태"),
                        fieldWithPath("data.address").type(JsonFieldType.VARIES).description("거주지"),
                        fieldWithPath("data.address.state").type(JsonFieldType.STRING).description("시도"),
                        fieldWithPath("data.address.city").type(JsonFieldType.STRING).description("시군구"),
                        fieldWithPath("data.address.town").type(JsonFieldType.STRING).description("음면동"),
                        fieldWithPath("data.like").type(JsonFieldType.NUMBER).description("상품 관심도").optional(),
                        fieldWithPath("data.userId").type(JsonFieldType.NUMBER).description("판매자 아이디").optional(),
                        fieldWithPath("data.images").type(JsonFieldType.VARIES).description("상품 이미지들").optional(),
                        fieldWithPath("data.images.[].id").type(JsonFieldType.NUMBER).description("상품 이미지 key").optional(),
                        fieldWithPath("data.images.[].url").type(JsonFieldType.STRING).description("상품 이미지 url").optional(),
                        fieldWithPath("data.images.[].userId").type(JsonFieldType.NUMBER).description("유저 이미지일 경우 유저 key").optional(),
                        fieldWithPath("data.images.[].productId").type(JsonFieldType.NUMBER).description("상품 이미지일 경우 상품 key").optional(),
                        fieldWithPath("data.reviews").type(JsonFieldType.VARIES).description("거래 후기들").optional()
                    )
                ));

    }

    @DisplayName("모든 상품 정보 가져오기")
    @Test
    void getAllProduct() throws Exception{

        when(productService.getAllProduct()).thenReturn(products);

        //when
        ResultActions actions = mockMvc.perform(
            get("/product")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8"));

        //then
        actions
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").doesNotExist())
            .andDo(document("getAllProduct",
                    responseFields(
                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공여부"),
                        fieldWithPath("message").type(JsonFieldType.STRING).type(JsonFieldType.NULL).description("요청 실패시 메시지"),
                        fieldWithPath("data").type(JsonFieldType.VARIES).description("요청 데이터(등록 된 상품 정보)"),

                        fieldWithPath("data.[].id").type(JsonFieldType.NUMBER).description("상품 key"),
                        fieldWithPath("data.[].title").type(JsonFieldType.STRING).description("상품 이름"),
                        fieldWithPath("data.[].category").type(JsonFieldType.VARIES).description("상품 카테고리"),
                        fieldWithPath("data.[].category.id").type(JsonFieldType.NUMBER).description("카테고리 아이디"),
                        fieldWithPath("data.[].category.categoryName").type(JsonFieldType.STRING).description("카테고리 이름"),
                        fieldWithPath("data.[].price").type(JsonFieldType.STRING).description("상품 가격"),
                        fieldWithPath("data.[].description").type(JsonFieldType.STRING).description("상품 설명"),
                        fieldWithPath("data.[].status").type(JsonFieldType.STRING).description("상품 상태"),
                        fieldWithPath("data.[].address").type(JsonFieldType.VARIES).description("거주지"),
                        fieldWithPath("data.[].address.state").type(JsonFieldType.STRING).description("시도"),
                        fieldWithPath("data.[].address.city").type(JsonFieldType.STRING).description("시군구"),
                        fieldWithPath("data.[].address.town").type(JsonFieldType.STRING).description("음면동"),
                        fieldWithPath("data.[].like").type(JsonFieldType.NUMBER).description("상품 관심도").optional(),
                        fieldWithPath("data.[].userId").type(JsonFieldType.NUMBER).description("판매자 아이디").optional(),
                        fieldWithPath("data.[].images").type(JsonFieldType.VARIES).description("상품 이미지들").optional(),
                        fieldWithPath("data.[].images.[].id").type(JsonFieldType.NUMBER).description("상품 이미지 key").optional(),
                        fieldWithPath("data.[].images.[].url").type(JsonFieldType.STRING).description("상품 이미지 url").optional(),
                        fieldWithPath("data.[].images.[].userId").type(JsonFieldType.NUMBER).description("유저 이미지일 경우 유저 key").optional(),
                        fieldWithPath("data.[].images.[].productId").type(JsonFieldType.NUMBER).description("상품 이미지일 경우 상품 key").optional(),
                        fieldWithPath("data.[].reviews").type(JsonFieldType.VARIES).description("거래 후기들").optional()
                    )
                ));
    }

    @DisplayName("상품 하나 정보 가져오기")
    @Test
    void getOneProduct() throws Exception{

        when(productService.getOneProduct(any(Long.class))).thenReturn(product);

        //when
        ResultActions actions = mockMvc.perform(
            RestDocumentationRequestBuilders.get("/product/{productId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8"));

        //then
        actions
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").doesNotExist())
            .andDo(document("getOneProduct",
                    pathParameters(
                        parameterWithName("productId").description("상품 key")
                    ),
                    responseFields(
                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공여부"),
                        fieldWithPath("message").type(JsonFieldType.STRING).type(JsonFieldType.NULL).description("요청 실패시 메시지"),
                        fieldWithPath("data").type(JsonFieldType.VARIES).description("요청 데이터(등록 된 상품 정보)"),

                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("상품 key"),
                        fieldWithPath("data.title").type(JsonFieldType.STRING).description("상품 이름"),
                        fieldWithPath("data.category").type(JsonFieldType.VARIES).description("상품 카테고리"),
                        fieldWithPath("data.category.id").type(JsonFieldType.NUMBER).description("카테고리 아이디"),
                        fieldWithPath("data.category.categoryName").type(JsonFieldType.STRING).description("카테고리 이름"),
                        fieldWithPath("data.price").type(JsonFieldType.STRING).description("상품 가격"),
                        fieldWithPath("data.description").type(JsonFieldType.STRING).description("상품 설명"),
                        fieldWithPath("data.status").type(JsonFieldType.STRING).description("상품 상태"),
                        fieldWithPath("data.address").type(JsonFieldType.VARIES).description("거주지"),
                        fieldWithPath("data.address.state").type(JsonFieldType.STRING).description("시도"),
                        fieldWithPath("data.address.city").type(JsonFieldType.STRING).description("시군구"),
                        fieldWithPath("data.address.town").type(JsonFieldType.STRING).description("음면동"),
                        fieldWithPath("data.like").type(JsonFieldType.NUMBER).description("상품 관심도").optional(),
                        fieldWithPath("data.userId").type(JsonFieldType.NUMBER).description("판매자 아이디").optional(),
                        fieldWithPath("data.images").type(JsonFieldType.VARIES).description("상품 이미지들").optional(),
                        fieldWithPath("data.images.[].id").type(JsonFieldType.NUMBER).description("상품 이미지 key").optional(),
                        fieldWithPath("data.images.[].url").type(JsonFieldType.STRING).description("상품 이미지 url").optional(),
                        fieldWithPath("data.images.[].userId").type(JsonFieldType.NUMBER).description("유저 이미지일 경우 유저 key").optional(),
                        fieldWithPath("data.images.[].productId").type(JsonFieldType.NUMBER).description("상품 이미지일 경우 상품 key").optional(),
                        fieldWithPath("data.reviews").type(JsonFieldType.VARIES).description("거래 후기들").optional()
                    )
                ));

    }

    @DisplayName("지역 기준으로 상품 조회하기(시/도) 테스트")
    @Test
    void getProductByState() throws Exception{

        when(productService.getProductByState(any(String.class))).thenReturn(products);

        //when
        ResultActions actions = mockMvc.perform(
            get("/product/state")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .param("state","경기도"));

        //then
        actions
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").doesNotExist())
            .andDo(document("getProductByState",
                    requestParameters(
                      parameterWithName("state").description("시/도 명")
                    ),
                    responseFields(
                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공여부"),
                        fieldWithPath("message").type(JsonFieldType.STRING).type(JsonFieldType.NULL).description("요청 실패시 메시지"),
                        fieldWithPath("data").type(JsonFieldType.VARIES).description("요청 데이터(등록 된 상품 정보)"),

                        fieldWithPath("data.[].id").type(JsonFieldType.NUMBER).description("상품 key"),
                        fieldWithPath("data.[].title").type(JsonFieldType.STRING).description("상품 이름"),
                        fieldWithPath("data.[].category").type(JsonFieldType.VARIES).description("상품 카테고리"),
                        fieldWithPath("data.[].category.id").type(JsonFieldType.NUMBER).description("카테고리 아이디"),
                        fieldWithPath("data.[].category.categoryName").type(JsonFieldType.STRING).description("카테고리 이름"),
                        fieldWithPath("data.[].price").type(JsonFieldType.STRING).description("상품 가격"),
                        fieldWithPath("data.[].description").type(JsonFieldType.STRING).description("상품 설명"),
                        fieldWithPath("data.[].status").type(JsonFieldType.STRING).description("상품 상태"),
                        fieldWithPath("data.[].address").type(JsonFieldType.VARIES).description("거주지"),
                        fieldWithPath("data.[].address.state").type(JsonFieldType.STRING).description("시도"),
                        fieldWithPath("data.[].address.city").type(JsonFieldType.STRING).description("시군구"),
                        fieldWithPath("data.[].address.town").type(JsonFieldType.STRING).description("음면동"),
                        fieldWithPath("data.[].like").type(JsonFieldType.NUMBER).description("상품 관심도").optional(),
                        fieldWithPath("data.[].userId").type(JsonFieldType.NUMBER).description("판매자 아이디").optional(),
                        fieldWithPath("data.[].images").type(JsonFieldType.VARIES).description("상품 이미지들").optional(),
                        fieldWithPath("data.[].images.[].id").type(JsonFieldType.NUMBER).description("상품 이미지 key").optional(),
                        fieldWithPath("data.[].images.[].url").type(JsonFieldType.STRING).description("상품 이미지 url").optional(),
                        fieldWithPath("data.[].images.[].userId").type(JsonFieldType.NUMBER).description("유저 이미지일 경우 유저 key").optional(),
                        fieldWithPath("data.[].images.[].productId").type(JsonFieldType.NUMBER).description("상품 이미지일 경우 상품 key").optional(),
                        fieldWithPath("data.[].reviews").type(JsonFieldType.VARIES).description("거래 후기들").optional()
                    )
                ));
    }

    @DisplayName("지역 기준으로 상품 조회하기(시/군/구) 테스트")
    @Test
    void getProductByCity() throws Exception{

        when(productService.getProductByCity(any(String.class))).thenReturn(products);

        //when
        ResultActions actions = mockMvc.perform(
            get("/product/city")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .param("city","부천시"));

        //then
        actions
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").doesNotExist())
            .andDo(document("getProductByCity",
                    requestParameters(
                      parameterWithName("city").description("시/군/구 명")
                    ),
                    responseFields(
                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공여부"),
                        fieldWithPath("message").type(JsonFieldType.STRING).type(JsonFieldType.NULL).description("요청 실패시 메시지"),
                        fieldWithPath("data").type(JsonFieldType.VARIES).description("요청 데이터(등록 된 상품 정보)"),

                        fieldWithPath("data.[].id").type(JsonFieldType.NUMBER).description("상품 key"),
                        fieldWithPath("data.[].title").type(JsonFieldType.STRING).description("상품 이름"),
                        fieldWithPath("data.[].category").type(JsonFieldType.VARIES).description("상품 카테고리"),
                        fieldWithPath("data.[].category.id").type(JsonFieldType.NUMBER).description("카테고리 아이디"),
                        fieldWithPath("data.[].category.categoryName").type(JsonFieldType.STRING).description("카테고리 이름"),
                        fieldWithPath("data.[].price").type(JsonFieldType.STRING).description("상품 가격"),
                        fieldWithPath("data.[].description").type(JsonFieldType.STRING).description("상품 설명"),
                        fieldWithPath("data.[].status").type(JsonFieldType.STRING).description("상품 상태"),
                        fieldWithPath("data.[].address").type(JsonFieldType.VARIES).description("거주지"),
                        fieldWithPath("data.[].address.state").type(JsonFieldType.STRING).description("시도"),
                        fieldWithPath("data.[].address.city").type(JsonFieldType.STRING).description("시군구"),
                        fieldWithPath("data.[].address.town").type(JsonFieldType.STRING).description("음면동"),
                        fieldWithPath("data.[].like").type(JsonFieldType.NUMBER).description("상품 관심도").optional(),
                        fieldWithPath("data.[].userId").type(JsonFieldType.NUMBER).description("판매자 아이디").optional(),
                        fieldWithPath("data.[].images").type(JsonFieldType.VARIES).description("상품 이미지들").optional(),
                        fieldWithPath("data.[].images.[].id").type(JsonFieldType.NUMBER).description("상품 이미지 key").optional(),
                        fieldWithPath("data.[].images.[].url").type(JsonFieldType.STRING).description("상품 이미지 url").optional(),
                        fieldWithPath("data.[].images.[].userId").type(JsonFieldType.NUMBER).description("유저 이미지일 경우 유저 key").optional(),
                        fieldWithPath("data.[].images.[].productId").type(JsonFieldType.NUMBER).description("상품 이미지일 경우 상품 key").optional(),
                        fieldWithPath("data.[].reviews").type(JsonFieldType.VARIES).description("거래 후기들").optional()
                    )
                ));
    }

    @DisplayName("지역 기준으로 상품 조회하기(읍/면/동) 테스트")
    @Test
    void getProductByTown() throws Exception{

        when(productService.getProductByTown(any(String.class))).thenReturn(products);

        //when
        ResultActions actions = mockMvc.perform(
            get("/product/town")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .param("town","상동"));

        //then
        actions
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").doesNotExist())
            .andDo(document("getProductByTown",
                    requestParameters(
                      parameterWithName("town").description("읍/면/동 명")
                    ),
                    responseFields(
                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공여부"),
                        fieldWithPath("message").type(JsonFieldType.STRING).type(JsonFieldType.NULL).description("요청 실패시 메시지"),
                        fieldWithPath("data").type(JsonFieldType.VARIES).description("요청 데이터(등록 된 상품 정보)"),

                        fieldWithPath("data.[].id").type(JsonFieldType.NUMBER).description("상품 key"),
                        fieldWithPath("data.[].title").type(JsonFieldType.STRING).description("상품 이름"),
                        fieldWithPath("data.[].category").type(JsonFieldType.VARIES).description("상품 카테고리"),
                        fieldWithPath("data.[].category.id").type(JsonFieldType.NUMBER).description("카테고리 아이디"),
                        fieldWithPath("data.[].category.categoryName").type(JsonFieldType.STRING).description("카테고리 이름"),
                        fieldWithPath("data.[].price").type(JsonFieldType.STRING).description("상품 가격"),
                        fieldWithPath("data.[].description").type(JsonFieldType.STRING).description("상품 설명"),
                        fieldWithPath("data.[].status").type(JsonFieldType.STRING).description("상품 상태"),
                        fieldWithPath("data.[].address").type(JsonFieldType.VARIES).description("거주지"),
                        fieldWithPath("data.[].address.state").type(JsonFieldType.STRING).description("시도"),
                        fieldWithPath("data.[].address.city").type(JsonFieldType.STRING).description("시군구"),
                        fieldWithPath("data.[].address.town").type(JsonFieldType.STRING).description("음면동"),
                        fieldWithPath("data.[].like").type(JsonFieldType.NUMBER).description("상품 관심도").optional(),
                        fieldWithPath("data.[].userId").type(JsonFieldType.NUMBER).description("판매자 아이디").optional(),
                        fieldWithPath("data.[].images").type(JsonFieldType.VARIES).description("상품 이미지들").optional(),
                        fieldWithPath("data.[].images.[].id").type(JsonFieldType.NUMBER).description("상품 이미지 key").optional(),
                        fieldWithPath("data.[].images.[].url").type(JsonFieldType.STRING).description("상품 이미지 url").optional(),
                        fieldWithPath("data.[].images.[].userId").type(JsonFieldType.NUMBER).description("유저 이미지일 경우 유저 key").optional(),
                        fieldWithPath("data.[].images.[].productId").type(JsonFieldType.NUMBER).description("상품 이미지일 경우 상품 key").optional(),
                        fieldWithPath("data.[].reviews").type(JsonFieldType.VARIES).description("거래 후기들").optional()
                    )
                ));
    }

    @DisplayName("상품 좋아요 증가시키기 테스트")
    @Test
    void plusLike() throws Exception{

        //given
        //when
        ResultActions actions = mockMvc.perform(
            RestDocumentationRequestBuilders.patch("/product/plusLike/{productId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8"));

        //then
        actions
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").doesNotExist())
            .andDo(document("plusLike",
                    pathParameters(
                        parameterWithName("productId").description("상품 key")
                    ),
                    responseFields(
                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공여부"),
                        fieldWithPath("message").type(JsonFieldType.STRING).type(JsonFieldType.NULL).description("요청 실패시 메시지"),
                        fieldWithPath("data").type(JsonFieldType.VARIES).type(JsonFieldType.NULL).description("응답 데이터")
                    )
                ));
    }

    @DisplayName("상품 좋아요 감소시키기 테스트")
    @Test
    void minusLike() throws Exception{

        //given
        //when
        ResultActions actions = mockMvc.perform(
            RestDocumentationRequestBuilders.patch("/product/minusLike/{productId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8"));

        //then
        actions
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").doesNotExist())
            .andDo(document("minusLike",
                    pathParameters(
                        parameterWithName("productId").description("상품 key")
                    ),
                    responseFields(
                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공여부"),
                        fieldWithPath("message").type(JsonFieldType.STRING).type(JsonFieldType.NULL).description("요청 실패시 메시지"),
                        fieldWithPath("data").type(JsonFieldType.VARIES).type(JsonFieldType.NULL).description("응답 데이터")
                    )
                ));
    }

    @DisplayName("상품 정보 수정하기 테스트")
    @Test
    void updateProduct() throws Exception {

        when(productService.updateProduct(any(Long.class), any(SaveProductRequest.class))).thenReturn(product);

        JSONObject jsonObject = new JSONObject()
            .put("title", "미개봉 모니터입니다.")
            .put("categoryId", 1L)
            .put("price", "64000")
            .put("description", "미개봉 모니터 싸게 팔아요~")
            .put("status", "SALE")
            .put("userId", 1L)
            .put("state", "경기도")
            .put("city","부천시")
            .put("town", "상동");

        //when
        ResultActions actions = mockMvc.perform(RestDocumentationRequestBuilders.
            patch("/product/update/{productId}", 1L)
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
            .andDo(document("updateProduct",
                    pathParameters(
                      parameterWithName("productId").description("상품 key")
                    ),
                    requestFields(
                        fieldWithPath("title").type(JsonFieldType.STRING).description("상품 이름"),
                        fieldWithPath("categoryId").type(JsonFieldType.NUMBER).description("카테고리 key"),
                        fieldWithPath("price").type(JsonFieldType.STRING).description("가격"),
                        fieldWithPath("description").type(JsonFieldType.STRING).description("상품 설명"),
                        fieldWithPath("status").type(JsonFieldType.STRING).description("상품 상태"),
                        fieldWithPath("userId").type(JsonFieldType.NUMBER).description("등록자 key"),
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
                        fieldWithPath("data.category.id").type(JsonFieldType.NUMBER).description("카테고리 아이디"),
                        fieldWithPath("data.category.categoryName").type(JsonFieldType.STRING).description("카테고리 이름"),
                        fieldWithPath("data.price").type(JsonFieldType.STRING).description("상품 가격"),
                        fieldWithPath("data.description").type(JsonFieldType.STRING).description("상품 설명"),
                        fieldWithPath("data.status").type(JsonFieldType.STRING).description("상품 상태"),
                        fieldWithPath("data.address").type(JsonFieldType.VARIES).description("거주지"),
                        fieldWithPath("data.address.state").type(JsonFieldType.STRING).description("시도"),
                        fieldWithPath("data.address.city").type(JsonFieldType.STRING).description("시군구"),
                        fieldWithPath("data.address.town").type(JsonFieldType.STRING).description("음면동"),
                        fieldWithPath("data.like").type(JsonFieldType.NUMBER).description("상품 관심도").optional(),
                        fieldWithPath("data.userId").type(JsonFieldType.NUMBER).description("판매자 아이디").optional(),
                        fieldWithPath("data.images").type(JsonFieldType.VARIES).description("상품 이미지들").optional(),
                        fieldWithPath("data.images.[].id").type(JsonFieldType.NUMBER).description("상품 이미지 key").optional(),
                        fieldWithPath("data.images.[].url").type(JsonFieldType.STRING).description("상품 이미지 url").optional(),
                        fieldWithPath("data.images.[].userId").type(JsonFieldType.NUMBER).description("유저 이미지일 경우 유저 key").optional(),
                        fieldWithPath("data.images.[].productId").type(JsonFieldType.NUMBER).description("상품 이미지일 경우 상품 key").optional(),
                        fieldWithPath("data.reviews").type(JsonFieldType.VARIES).description("거래 후기들").optional()
                    )
                ));

    }

    @DisplayName("상품 취소 상태로 변경 테스트")
    @Test
    void cancelProduct() throws Exception{

        //given
        //when
        ResultActions actions = mockMvc.perform(
            RestDocumentationRequestBuilders.patch("/product/cancel/{productId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8"));

        //then
        actions
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").doesNotExist())
            .andDo(document("cancelProduct",
                    pathParameters(
                        parameterWithName("productId").description("상품 key")
                    ),
                    responseFields(
                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공여부"),
                        fieldWithPath("message").type(JsonFieldType.STRING).type(JsonFieldType.NULL).description("요청 실패시 메시지"),
                        fieldWithPath("data").type(JsonFieldType.VARIES).type(JsonFieldType.NULL).description("응답 데이터")
                    )
                ));
    }

    @DisplayName("상품 완료 상태로 변경 테스트")
    @Test
    void completeProduct() throws Exception{

        //given
        //when
        ResultActions actions = mockMvc.perform(
            RestDocumentationRequestBuilders.patch("/product/complete/{productId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8"));

        //then
        actions
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").doesNotExist())
            .andDo(document("completeProduct",
                    pathParameters(
                        parameterWithName("productId").description("상품 key")
                    ),
                    responseFields(
                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공여부"),
                        fieldWithPath("message").type(JsonFieldType.STRING).type(JsonFieldType.NULL).description("요청 실패시 메시지"),
                        fieldWithPath("data").type(JsonFieldType.VARIES).type(JsonFieldType.NULL).description("응답 데이터")
                    )
                ));
    }

    @DisplayName("상품 판매 상태로 변경 테스트")
    @Test
    void saleProduct() throws Exception{

        //given
        //when
        ResultActions actions = mockMvc.perform(
            RestDocumentationRequestBuilders.patch("/product/sale/{productId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8"));

        //then
        actions
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").doesNotExist())
            .andDo(document("saleProduct",
                    pathParameters(
                        parameterWithName("productId").description("상품 key")
                    ),
                    responseFields(
                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공여부"),
                        fieldWithPath("message").type(JsonFieldType.STRING).type(JsonFieldType.NULL).description("요청 실패시 메시지"),
                        fieldWithPath("data").type(JsonFieldType.VARIES).type(JsonFieldType.NULL).description("응답 데이터")
                    )
                ));
    }

    @DisplayName("상품 이미지 추가하기 테스트")
    @Test
    void addProductImage() throws Exception {

        when(productService.addProductImage(any(Long.class), any(List.class))).thenReturn(product);
        List<MockMultipartFile> files = new ArrayList<>();
        files.add(new MockMultipartFile("img", "img.png".getBytes()));

        //when
        ResultActions actions = mockMvc.perform(
            RestDocumentationRequestBuilders.multipart("/product/image/{productId}", 1L)
                .file("multipartFiles", files.get(0).getBytes())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8"));

        //then
        actions
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").doesNotExist())
            .andDo(document("addProductImage",
                    pathParameters(
                        parameterWithName("productId").description("상품 key")
                    ),
                    requestParts(
                        partWithName("multipartFiles").description("추가 이미지 file")
                    ),
                    responseFields(
                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공여부"),
                        fieldWithPath("message").type(JsonFieldType.STRING).type(JsonFieldType.NULL).description("요청 실패시 메시지"),
                        fieldWithPath("data").type(JsonFieldType.VARIES).description("요청 데이터(등록 된 상품 정보)"),

                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("상품 key"),
                        fieldWithPath("data.title").type(JsonFieldType.STRING).description("상품 이름"),
                        fieldWithPath("data.category").type(JsonFieldType.VARIES).description("상품 카테고리"),
                        fieldWithPath("data.category.id").type(JsonFieldType.NUMBER).description("카테고리 아이디"),
                        fieldWithPath("data.category.categoryName").type(JsonFieldType.STRING).description("카테고리 이름"),
                        fieldWithPath("data.price").type(JsonFieldType.STRING).description("상품 가격"),
                        fieldWithPath("data.description").type(JsonFieldType.STRING).description("상품 설명"),
                        fieldWithPath("data.status").type(JsonFieldType.STRING).description("상품 상태"),
                        fieldWithPath("data.address").type(JsonFieldType.VARIES).description("거주지"),
                        fieldWithPath("data.address.state").type(JsonFieldType.STRING).description("시도"),
                        fieldWithPath("data.address.city").type(JsonFieldType.STRING).description("시군구"),
                        fieldWithPath("data.address.town").type(JsonFieldType.STRING).description("음면동"),
                        fieldWithPath("data.like").type(JsonFieldType.NUMBER).description("상품 관심도").optional(),
                        fieldWithPath("data.userId").type(JsonFieldType.NUMBER).description("판매자 아이디").optional(),
                        fieldWithPath("data.images").type(JsonFieldType.VARIES).description("상품 이미지들").optional(),
                        fieldWithPath("data.images.[].id").type(JsonFieldType.NUMBER).description("상품 이미지 key").optional(),
                        fieldWithPath("data.images.[].url").type(JsonFieldType.STRING).description("상품 이미지 url").optional(),
                        fieldWithPath("data.images.[].userId").type(JsonFieldType.NUMBER).description("유저 이미지일 경우 유저 key").optional(),
                        fieldWithPath("data.images.[].productId").type(JsonFieldType.NUMBER).description("상품 이미지일 경우 상품 key").optional(),
                        fieldWithPath("data.reviews").type(JsonFieldType.VARIES).description("거래 후기들").optional()
                    )
                ));

    }


    @DisplayName("상품 이미지 삭제하기 테스트")
    @Test
    void deleteProductImage() throws Exception {

        //given
        when(productService.deleteProductImage(any(Long.class), any(Long.class))).thenReturn(product);

        //when
        ResultActions actions = mockMvc.perform(
            RestDocumentationRequestBuilders.delete("/product/image/{productId}/{imageId}", 1L, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8"));

        //then
        actions
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").doesNotExist())
            .andDo(document("deleteProductImage",
                    pathParameters(
                        parameterWithName("productId").description("상품 key"),
                        parameterWithName("imageId").description("이미지 key")
                    ),
                    responseFields(
                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공여부"),
                        fieldWithPath("message").type(JsonFieldType.STRING).type(JsonFieldType.NULL).description("요청 실패시 메시지"),
                        fieldWithPath("data").type(JsonFieldType.VARIES).description("요청 데이터(등록 된 상품 정보)"),

                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("상품 key"),
                        fieldWithPath("data.title").type(JsonFieldType.STRING).description("상품 이름"),
                        fieldWithPath("data.category").type(JsonFieldType.VARIES).description("상품 카테고리"),
                        fieldWithPath("data.category.id").type(JsonFieldType.NUMBER).description("카테고리 아이디"),
                        fieldWithPath("data.category.categoryName").type(JsonFieldType.STRING).description("카테고리 이름"),
                        fieldWithPath("data.price").type(JsonFieldType.STRING).description("상품 가격"),
                        fieldWithPath("data.description").type(JsonFieldType.STRING).description("상품 설명"),
                        fieldWithPath("data.status").type(JsonFieldType.STRING).description("상품 상태"),
                        fieldWithPath("data.address").type(JsonFieldType.VARIES).description("거주지"),
                        fieldWithPath("data.address.state").type(JsonFieldType.STRING).description("시도"),
                        fieldWithPath("data.address.city").type(JsonFieldType.STRING).description("시군구"),
                        fieldWithPath("data.address.town").type(JsonFieldType.STRING).description("음면동"),
                        fieldWithPath("data.like").type(JsonFieldType.NUMBER).description("상품 관심도").optional(),
                        fieldWithPath("data.userId").type(JsonFieldType.NUMBER).description("판매자 아이디").optional(),
                        fieldWithPath("data.images").type(JsonFieldType.VARIES).description("상품 이미지들").optional(),
                        fieldWithPath("data.images.[].id").type(JsonFieldType.NUMBER).description("상품 이미지 key").optional(),
                        fieldWithPath("data.images.[].url").type(JsonFieldType.STRING).description("상품 이미지 url").optional(),
                        fieldWithPath("data.images.[].userId").type(JsonFieldType.NUMBER).description("유저 이미지일 경우 유저 key").optional(),
                        fieldWithPath("data.images.[].productId").type(JsonFieldType.NUMBER).description("상품 이미지일 경우 상품 key").optional(),
                        fieldWithPath("data.reviews").type(JsonFieldType.VARIES).description("거래 후기들").optional()
                    )
                ));
    }

    @DisplayName("상품의 리뷰 가져오기")
    @Test
    void getAllReviewProduct() throws Exception {

        //given
        when(productService.getAllReviewProduct(any(Long.class))).thenReturn(reviews);

        //when
        ResultActions actions = mockMvc.perform(
            RestDocumentationRequestBuilders.get("/product/review/{productId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8"));

        //then
        actions
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").doesNotExist())
            .andDo(document("getAllReviewProduct",
                    pathParameters(
                        parameterWithName("productId").description("상품 key")
                    ),
                    responseFields(
                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공여부"),
                        fieldWithPath("message").type(JsonFieldType.STRING).type(JsonFieldType.NULL).description("요청 실패시 메시지"),
                        fieldWithPath("data").type(JsonFieldType.VARIES).description("요청 데이터(거래 후기들)"),
                        fieldWithPath("data.[].id").type(JsonFieldType.NUMBER).description("후기 key"),
                        fieldWithPath("data.[].content").type(JsonFieldType.STRING).description("후기 내용"),
                        fieldWithPath("data.[].userId").type(JsonFieldType.NUMBER).description("후기 작성자 key"),
                        fieldWithPath("data.[].productId").type(JsonFieldType.NUMBER).description("후기 상품 key").optional()
                    )
                ));
    }
}