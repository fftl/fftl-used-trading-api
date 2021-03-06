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
        categories.add(Category.builder().id(1L).categoryName("?????????/??????").build());
        categories.add(Category.builder().id(2L).categoryName("??????").build());

        category = Category.builder().id(1L).categoryName("?????????/??????").build();

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
    void saveProduct() throws Exception {

        when(productService.saveProduct(any(SaveProductRequest.class))).thenReturn(product);
        List<MockMultipartFile> files = new ArrayList<>();
        files.add(new MockMultipartFile("img", "img.png".getBytes()));

        JSONObject jsonObject = new JSONObject()
            .put("title", "????????? ??????????????????.")
            .put("categoryId", 1L)
            .put("price", "80000")
            .put("description", "????????? ????????? ?????? ?????????~")
            .put("status", "SALE")
            .put("userId", 1L)
            .put("state", "?????????")
            .put("city","?????????")
            .put("town", "??????");

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
                        fieldWithPath("title").type(JsonFieldType.STRING).description("?????? ??????"),
                        fieldWithPath("categoryId").type(JsonFieldType.NUMBER).description("???????????? key"),
                        fieldWithPath("price").type(JsonFieldType.STRING).description("??????"),
                        fieldWithPath("description").type(JsonFieldType.STRING).description("?????? ??????"),
                        fieldWithPath("status").type(JsonFieldType.STRING).description("?????? ??????"),
                        fieldWithPath("userId").type(JsonFieldType.NUMBER).description("????????? key"),
                        fieldWithPath("state").type(JsonFieldType.STRING).description("?????????(??????)"),
                        fieldWithPath("city").type(JsonFieldType.STRING).description("?????????(?????????)"),
                        fieldWithPath("town").type(JsonFieldType.STRING).description("?????????(?????????)"),
                        fieldWithPath("files").type(JsonFieldType.VARIES).description("?????? ????????????").optional()
                    ),
                    responseFields(
                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("?????? ????????????"),
                        fieldWithPath("message").type(JsonFieldType.STRING).type(JsonFieldType.NULL).description("?????? ????????? ?????????"),
                        fieldWithPath("data").type(JsonFieldType.VARIES).description("?????? ?????????(?????? ??? ?????? ??????)"),

                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("?????? key"),
                        fieldWithPath("data.title").type(JsonFieldType.STRING).description("?????? ??????"),
                        fieldWithPath("data.category").type(JsonFieldType.VARIES).description("?????? ????????????"),
                        fieldWithPath("data.category.id").type(JsonFieldType.NUMBER).description("???????????? ?????????"),
                        fieldWithPath("data.category.categoryName").type(JsonFieldType.STRING).description("???????????? ??????"),
                        fieldWithPath("data.price").type(JsonFieldType.STRING).description("?????? ??????"),
                        fieldWithPath("data.description").type(JsonFieldType.STRING).description("?????? ??????"),
                        fieldWithPath("data.status").type(JsonFieldType.STRING).description("?????? ??????"),
                        fieldWithPath("data.address").type(JsonFieldType.VARIES).description("?????????"),
                        fieldWithPath("data.address.state").type(JsonFieldType.STRING).description("??????"),
                        fieldWithPath("data.address.city").type(JsonFieldType.STRING).description("?????????"),
                        fieldWithPath("data.address.town").type(JsonFieldType.STRING).description("?????????"),
                        fieldWithPath("data.like").type(JsonFieldType.NUMBER).description("?????? ?????????").optional(),
                        fieldWithPath("data.userId").type(JsonFieldType.NUMBER).description("????????? ?????????").optional(),
                        fieldWithPath("data.images").type(JsonFieldType.VARIES).description("?????? ????????????").optional(),
                        fieldWithPath("data.images.[].id").type(JsonFieldType.NUMBER).description("?????? ????????? key").optional(),
                        fieldWithPath("data.images.[].url").type(JsonFieldType.STRING).description("?????? ????????? url").optional(),
                        fieldWithPath("data.images.[].userId").type(JsonFieldType.NUMBER).description("?????? ???????????? ?????? ?????? key").optional(),
                        fieldWithPath("data.images.[].productId").type(JsonFieldType.NUMBER).description("?????? ???????????? ?????? ?????? key").optional(),
                        fieldWithPath("data.reviews").type(JsonFieldType.VARIES).description("?????? ?????????").optional()
                    )
                ));

    }

    @DisplayName("?????? ?????? ?????? ????????????")
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
                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("?????? ????????????"),
                        fieldWithPath("message").type(JsonFieldType.STRING).type(JsonFieldType.NULL).description("?????? ????????? ?????????"),
                        fieldWithPath("data").type(JsonFieldType.VARIES).description("?????? ?????????(?????? ??? ?????? ??????)"),

                        fieldWithPath("data.[].id").type(JsonFieldType.NUMBER).description("?????? key"),
                        fieldWithPath("data.[].title").type(JsonFieldType.STRING).description("?????? ??????"),
                        fieldWithPath("data.[].category").type(JsonFieldType.VARIES).description("?????? ????????????"),
                        fieldWithPath("data.[].category.id").type(JsonFieldType.NUMBER).description("???????????? ?????????"),
                        fieldWithPath("data.[].category.categoryName").type(JsonFieldType.STRING).description("???????????? ??????"),
                        fieldWithPath("data.[].price").type(JsonFieldType.STRING).description("?????? ??????"),
                        fieldWithPath("data.[].description").type(JsonFieldType.STRING).description("?????? ??????"),
                        fieldWithPath("data.[].status").type(JsonFieldType.STRING).description("?????? ??????"),
                        fieldWithPath("data.[].address").type(JsonFieldType.VARIES).description("?????????"),
                        fieldWithPath("data.[].address.state").type(JsonFieldType.STRING).description("??????"),
                        fieldWithPath("data.[].address.city").type(JsonFieldType.STRING).description("?????????"),
                        fieldWithPath("data.[].address.town").type(JsonFieldType.STRING).description("?????????"),
                        fieldWithPath("data.[].like").type(JsonFieldType.NUMBER).description("?????? ?????????").optional(),
                        fieldWithPath("data.[].userId").type(JsonFieldType.NUMBER).description("????????? ?????????").optional(),
                        fieldWithPath("data.[].images").type(JsonFieldType.VARIES).description("?????? ????????????").optional(),
                        fieldWithPath("data.[].images.[].id").type(JsonFieldType.NUMBER).description("?????? ????????? key").optional(),
                        fieldWithPath("data.[].images.[].url").type(JsonFieldType.STRING).description("?????? ????????? url").optional(),
                        fieldWithPath("data.[].images.[].userId").type(JsonFieldType.NUMBER).description("?????? ???????????? ?????? ?????? key").optional(),
                        fieldWithPath("data.[].images.[].productId").type(JsonFieldType.NUMBER).description("?????? ???????????? ?????? ?????? key").optional(),
                        fieldWithPath("data.[].reviews").type(JsonFieldType.VARIES).description("?????? ?????????").optional()
                    )
                ));
    }

    @DisplayName("?????? ?????? ?????? ????????????")
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
                        parameterWithName("productId").description("?????? key")
                    ),
                    responseFields(
                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("?????? ????????????"),
                        fieldWithPath("message").type(JsonFieldType.STRING).type(JsonFieldType.NULL).description("?????? ????????? ?????????"),
                        fieldWithPath("data").type(JsonFieldType.VARIES).description("?????? ?????????(?????? ??? ?????? ??????)"),

                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("?????? key"),
                        fieldWithPath("data.title").type(JsonFieldType.STRING).description("?????? ??????"),
                        fieldWithPath("data.category").type(JsonFieldType.VARIES).description("?????? ????????????"),
                        fieldWithPath("data.category.id").type(JsonFieldType.NUMBER).description("???????????? ?????????"),
                        fieldWithPath("data.category.categoryName").type(JsonFieldType.STRING).description("???????????? ??????"),
                        fieldWithPath("data.price").type(JsonFieldType.STRING).description("?????? ??????"),
                        fieldWithPath("data.description").type(JsonFieldType.STRING).description("?????? ??????"),
                        fieldWithPath("data.status").type(JsonFieldType.STRING).description("?????? ??????"),
                        fieldWithPath("data.address").type(JsonFieldType.VARIES).description("?????????"),
                        fieldWithPath("data.address.state").type(JsonFieldType.STRING).description("??????"),
                        fieldWithPath("data.address.city").type(JsonFieldType.STRING).description("?????????"),
                        fieldWithPath("data.address.town").type(JsonFieldType.STRING).description("?????????"),
                        fieldWithPath("data.like").type(JsonFieldType.NUMBER).description("?????? ?????????").optional(),
                        fieldWithPath("data.userId").type(JsonFieldType.NUMBER).description("????????? ?????????").optional(),
                        fieldWithPath("data.images").type(JsonFieldType.VARIES).description("?????? ????????????").optional(),
                        fieldWithPath("data.images.[].id").type(JsonFieldType.NUMBER).description("?????? ????????? key").optional(),
                        fieldWithPath("data.images.[].url").type(JsonFieldType.STRING).description("?????? ????????? url").optional(),
                        fieldWithPath("data.images.[].userId").type(JsonFieldType.NUMBER).description("?????? ???????????? ?????? ?????? key").optional(),
                        fieldWithPath("data.images.[].productId").type(JsonFieldType.NUMBER).description("?????? ???????????? ?????? ?????? key").optional(),
                        fieldWithPath("data.reviews").type(JsonFieldType.VARIES).description("?????? ?????????").optional()
                    )
                ));

    }

    @DisplayName("?????? ???????????? ?????? ????????????(???/???) ?????????")
    @Test
    void getProductByState() throws Exception{

        when(productService.getProductByState(any(String.class))).thenReturn(products);

        //when
        ResultActions actions = mockMvc.perform(
            get("/product/state")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .param("state","?????????"));

        //then
        actions
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").doesNotExist())
            .andDo(document("getProductByState",
                    requestParameters(
                      parameterWithName("state").description("???/??? ???")
                    ),
                    responseFields(
                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("?????? ????????????"),
                        fieldWithPath("message").type(JsonFieldType.STRING).type(JsonFieldType.NULL).description("?????? ????????? ?????????"),
                        fieldWithPath("data").type(JsonFieldType.VARIES).description("?????? ?????????(?????? ??? ?????? ??????)"),

                        fieldWithPath("data.[].id").type(JsonFieldType.NUMBER).description("?????? key"),
                        fieldWithPath("data.[].title").type(JsonFieldType.STRING).description("?????? ??????"),
                        fieldWithPath("data.[].category").type(JsonFieldType.VARIES).description("?????? ????????????"),
                        fieldWithPath("data.[].category.id").type(JsonFieldType.NUMBER).description("???????????? ?????????"),
                        fieldWithPath("data.[].category.categoryName").type(JsonFieldType.STRING).description("???????????? ??????"),
                        fieldWithPath("data.[].price").type(JsonFieldType.STRING).description("?????? ??????"),
                        fieldWithPath("data.[].description").type(JsonFieldType.STRING).description("?????? ??????"),
                        fieldWithPath("data.[].status").type(JsonFieldType.STRING).description("?????? ??????"),
                        fieldWithPath("data.[].address").type(JsonFieldType.VARIES).description("?????????"),
                        fieldWithPath("data.[].address.state").type(JsonFieldType.STRING).description("??????"),
                        fieldWithPath("data.[].address.city").type(JsonFieldType.STRING).description("?????????"),
                        fieldWithPath("data.[].address.town").type(JsonFieldType.STRING).description("?????????"),
                        fieldWithPath("data.[].like").type(JsonFieldType.NUMBER).description("?????? ?????????").optional(),
                        fieldWithPath("data.[].userId").type(JsonFieldType.NUMBER).description("????????? ?????????").optional(),
                        fieldWithPath("data.[].images").type(JsonFieldType.VARIES).description("?????? ????????????").optional(),
                        fieldWithPath("data.[].images.[].id").type(JsonFieldType.NUMBER).description("?????? ????????? key").optional(),
                        fieldWithPath("data.[].images.[].url").type(JsonFieldType.STRING).description("?????? ????????? url").optional(),
                        fieldWithPath("data.[].images.[].userId").type(JsonFieldType.NUMBER).description("?????? ???????????? ?????? ?????? key").optional(),
                        fieldWithPath("data.[].images.[].productId").type(JsonFieldType.NUMBER).description("?????? ???????????? ?????? ?????? key").optional(),
                        fieldWithPath("data.[].reviews").type(JsonFieldType.VARIES).description("?????? ?????????").optional()
                    )
                ));
    }

    @DisplayName("?????? ???????????? ?????? ????????????(???/???/???) ?????????")
    @Test
    void getProductByCity() throws Exception{

        when(productService.getProductByCity(any(String.class))).thenReturn(products);

        //when
        ResultActions actions = mockMvc.perform(
            get("/product/city")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .param("city","?????????"));

        //then
        actions
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").doesNotExist())
            .andDo(document("getProductByCity",
                    requestParameters(
                      parameterWithName("city").description("???/???/??? ???")
                    ),
                    responseFields(
                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("?????? ????????????"),
                        fieldWithPath("message").type(JsonFieldType.STRING).type(JsonFieldType.NULL).description("?????? ????????? ?????????"),
                        fieldWithPath("data").type(JsonFieldType.VARIES).description("?????? ?????????(?????? ??? ?????? ??????)"),

                        fieldWithPath("data.[].id").type(JsonFieldType.NUMBER).description("?????? key"),
                        fieldWithPath("data.[].title").type(JsonFieldType.STRING).description("?????? ??????"),
                        fieldWithPath("data.[].category").type(JsonFieldType.VARIES).description("?????? ????????????"),
                        fieldWithPath("data.[].category.id").type(JsonFieldType.NUMBER).description("???????????? ?????????"),
                        fieldWithPath("data.[].category.categoryName").type(JsonFieldType.STRING).description("???????????? ??????"),
                        fieldWithPath("data.[].price").type(JsonFieldType.STRING).description("?????? ??????"),
                        fieldWithPath("data.[].description").type(JsonFieldType.STRING).description("?????? ??????"),
                        fieldWithPath("data.[].status").type(JsonFieldType.STRING).description("?????? ??????"),
                        fieldWithPath("data.[].address").type(JsonFieldType.VARIES).description("?????????"),
                        fieldWithPath("data.[].address.state").type(JsonFieldType.STRING).description("??????"),
                        fieldWithPath("data.[].address.city").type(JsonFieldType.STRING).description("?????????"),
                        fieldWithPath("data.[].address.town").type(JsonFieldType.STRING).description("?????????"),
                        fieldWithPath("data.[].like").type(JsonFieldType.NUMBER).description("?????? ?????????").optional(),
                        fieldWithPath("data.[].userId").type(JsonFieldType.NUMBER).description("????????? ?????????").optional(),
                        fieldWithPath("data.[].images").type(JsonFieldType.VARIES).description("?????? ????????????").optional(),
                        fieldWithPath("data.[].images.[].id").type(JsonFieldType.NUMBER).description("?????? ????????? key").optional(),
                        fieldWithPath("data.[].images.[].url").type(JsonFieldType.STRING).description("?????? ????????? url").optional(),
                        fieldWithPath("data.[].images.[].userId").type(JsonFieldType.NUMBER).description("?????? ???????????? ?????? ?????? key").optional(),
                        fieldWithPath("data.[].images.[].productId").type(JsonFieldType.NUMBER).description("?????? ???????????? ?????? ?????? key").optional(),
                        fieldWithPath("data.[].reviews").type(JsonFieldType.VARIES).description("?????? ?????????").optional()
                    )
                ));
    }

    @DisplayName("?????? ???????????? ?????? ????????????(???/???/???) ?????????")
    @Test
    void getProductByTown() throws Exception{

        when(productService.getProductByTown(any(String.class))).thenReturn(products);

        //when
        ResultActions actions = mockMvc.perform(
            get("/product/town")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .param("town","??????"));

        //then
        actions
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").doesNotExist())
            .andDo(document("getProductByTown",
                    requestParameters(
                      parameterWithName("town").description("???/???/??? ???")
                    ),
                    responseFields(
                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("?????? ????????????"),
                        fieldWithPath("message").type(JsonFieldType.STRING).type(JsonFieldType.NULL).description("?????? ????????? ?????????"),
                        fieldWithPath("data").type(JsonFieldType.VARIES).description("?????? ?????????(?????? ??? ?????? ??????)"),

                        fieldWithPath("data.[].id").type(JsonFieldType.NUMBER).description("?????? key"),
                        fieldWithPath("data.[].title").type(JsonFieldType.STRING).description("?????? ??????"),
                        fieldWithPath("data.[].category").type(JsonFieldType.VARIES).description("?????? ????????????"),
                        fieldWithPath("data.[].category.id").type(JsonFieldType.NUMBER).description("???????????? ?????????"),
                        fieldWithPath("data.[].category.categoryName").type(JsonFieldType.STRING).description("???????????? ??????"),
                        fieldWithPath("data.[].price").type(JsonFieldType.STRING).description("?????? ??????"),
                        fieldWithPath("data.[].description").type(JsonFieldType.STRING).description("?????? ??????"),
                        fieldWithPath("data.[].status").type(JsonFieldType.STRING).description("?????? ??????"),
                        fieldWithPath("data.[].address").type(JsonFieldType.VARIES).description("?????????"),
                        fieldWithPath("data.[].address.state").type(JsonFieldType.STRING).description("??????"),
                        fieldWithPath("data.[].address.city").type(JsonFieldType.STRING).description("?????????"),
                        fieldWithPath("data.[].address.town").type(JsonFieldType.STRING).description("?????????"),
                        fieldWithPath("data.[].like").type(JsonFieldType.NUMBER).description("?????? ?????????").optional(),
                        fieldWithPath("data.[].userId").type(JsonFieldType.NUMBER).description("????????? ?????????").optional(),
                        fieldWithPath("data.[].images").type(JsonFieldType.VARIES).description("?????? ????????????").optional(),
                        fieldWithPath("data.[].images.[].id").type(JsonFieldType.NUMBER).description("?????? ????????? key").optional(),
                        fieldWithPath("data.[].images.[].url").type(JsonFieldType.STRING).description("?????? ????????? url").optional(),
                        fieldWithPath("data.[].images.[].userId").type(JsonFieldType.NUMBER).description("?????? ???????????? ?????? ?????? key").optional(),
                        fieldWithPath("data.[].images.[].productId").type(JsonFieldType.NUMBER).description("?????? ???????????? ?????? ?????? key").optional(),
                        fieldWithPath("data.[].reviews").type(JsonFieldType.VARIES).description("?????? ?????????").optional()
                    )
                ));
    }

    @DisplayName("?????? ????????? ??????????????? ?????????")
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
                        parameterWithName("productId").description("?????? key")
                    ),
                    responseFields(
                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("?????? ????????????"),
                        fieldWithPath("message").type(JsonFieldType.STRING).type(JsonFieldType.NULL).description("?????? ????????? ?????????"),
                        fieldWithPath("data").type(JsonFieldType.VARIES).type(JsonFieldType.NULL).description("?????? ?????????")
                    )
                ));
    }

    @DisplayName("?????? ????????? ??????????????? ?????????")
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
                        parameterWithName("productId").description("?????? key")
                    ),
                    responseFields(
                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("?????? ????????????"),
                        fieldWithPath("message").type(JsonFieldType.STRING).type(JsonFieldType.NULL).description("?????? ????????? ?????????"),
                        fieldWithPath("data").type(JsonFieldType.VARIES).type(JsonFieldType.NULL).description("?????? ?????????")
                    )
                ));
    }

    @DisplayName("?????? ?????? ???????????? ?????????")
    @Test
    void updateProduct() throws Exception {

        when(productService.updateProduct(any(Long.class), any(SaveProductRequest.class))).thenReturn(product);

        JSONObject jsonObject = new JSONObject()
            .put("title", "????????? ??????????????????.")
            .put("categoryId", 1L)
            .put("price", "64000")
            .put("description", "????????? ????????? ?????? ?????????~")
            .put("status", "SALE")
            .put("userId", 1L)
            .put("state", "?????????")
            .put("city","?????????")
            .put("town", "??????");

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
                      parameterWithName("productId").description("?????? key")
                    ),
                    requestFields(
                        fieldWithPath("title").type(JsonFieldType.STRING).description("?????? ??????"),
                        fieldWithPath("categoryId").type(JsonFieldType.NUMBER).description("???????????? key"),
                        fieldWithPath("price").type(JsonFieldType.STRING).description("??????"),
                        fieldWithPath("description").type(JsonFieldType.STRING).description("?????? ??????"),
                        fieldWithPath("status").type(JsonFieldType.STRING).description("?????? ??????"),
                        fieldWithPath("userId").type(JsonFieldType.NUMBER).description("????????? key"),
                        fieldWithPath("state").type(JsonFieldType.STRING).description("?????????(??????)"),
                        fieldWithPath("city").type(JsonFieldType.STRING).description("?????????(?????????)"),
                        fieldWithPath("town").type(JsonFieldType.STRING).description("?????????(?????????)"),
                        fieldWithPath("files").type(JsonFieldType.VARIES).description("?????? ????????????").optional()
                    ),
                    responseFields(
                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("?????? ????????????"),
                        fieldWithPath("message").type(JsonFieldType.STRING).type(JsonFieldType.NULL).description("?????? ????????? ?????????"),
                        fieldWithPath("data").type(JsonFieldType.VARIES).description("?????? ?????????(?????? ??? ?????? ??????)"),

                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("?????? key"),
                        fieldWithPath("data.title").type(JsonFieldType.STRING).description("?????? ??????"),
                        fieldWithPath("data.category").type(JsonFieldType.VARIES).description("?????? ????????????"),
                        fieldWithPath("data.category.id").type(JsonFieldType.NUMBER).description("???????????? ?????????"),
                        fieldWithPath("data.category.categoryName").type(JsonFieldType.STRING).description("???????????? ??????"),
                        fieldWithPath("data.price").type(JsonFieldType.STRING).description("?????? ??????"),
                        fieldWithPath("data.description").type(JsonFieldType.STRING).description("?????? ??????"),
                        fieldWithPath("data.status").type(JsonFieldType.STRING).description("?????? ??????"),
                        fieldWithPath("data.address").type(JsonFieldType.VARIES).description("?????????"),
                        fieldWithPath("data.address.state").type(JsonFieldType.STRING).description("??????"),
                        fieldWithPath("data.address.city").type(JsonFieldType.STRING).description("?????????"),
                        fieldWithPath("data.address.town").type(JsonFieldType.STRING).description("?????????"),
                        fieldWithPath("data.like").type(JsonFieldType.NUMBER).description("?????? ?????????").optional(),
                        fieldWithPath("data.userId").type(JsonFieldType.NUMBER).description("????????? ?????????").optional(),
                        fieldWithPath("data.images").type(JsonFieldType.VARIES).description("?????? ????????????").optional(),
                        fieldWithPath("data.images.[].id").type(JsonFieldType.NUMBER).description("?????? ????????? key").optional(),
                        fieldWithPath("data.images.[].url").type(JsonFieldType.STRING).description("?????? ????????? url").optional(),
                        fieldWithPath("data.images.[].userId").type(JsonFieldType.NUMBER).description("?????? ???????????? ?????? ?????? key").optional(),
                        fieldWithPath("data.images.[].productId").type(JsonFieldType.NUMBER).description("?????? ???????????? ?????? ?????? key").optional(),
                        fieldWithPath("data.reviews").type(JsonFieldType.VARIES).description("?????? ?????????").optional()
                    )
                ));

    }

    @DisplayName("?????? ?????? ????????? ?????? ?????????")
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
                        parameterWithName("productId").description("?????? key")
                    ),
                    responseFields(
                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("?????? ????????????"),
                        fieldWithPath("message").type(JsonFieldType.STRING).type(JsonFieldType.NULL).description("?????? ????????? ?????????"),
                        fieldWithPath("data").type(JsonFieldType.VARIES).type(JsonFieldType.NULL).description("?????? ?????????")
                    )
                ));
    }

    @DisplayName("?????? ?????? ????????? ?????? ?????????")
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
                        parameterWithName("productId").description("?????? key")
                    ),
                    responseFields(
                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("?????? ????????????"),
                        fieldWithPath("message").type(JsonFieldType.STRING).type(JsonFieldType.NULL).description("?????? ????????? ?????????"),
                        fieldWithPath("data").type(JsonFieldType.VARIES).type(JsonFieldType.NULL).description("?????? ?????????")
                    )
                ));
    }

    @DisplayName("?????? ?????? ????????? ?????? ?????????")
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
                        parameterWithName("productId").description("?????? key")
                    ),
                    responseFields(
                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("?????? ????????????"),
                        fieldWithPath("message").type(JsonFieldType.STRING).type(JsonFieldType.NULL).description("?????? ????????? ?????????"),
                        fieldWithPath("data").type(JsonFieldType.VARIES).type(JsonFieldType.NULL).description("?????? ?????????")
                    )
                ));
    }

    @DisplayName("?????? ????????? ???????????? ?????????")
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
                        parameterWithName("productId").description("?????? key")
                    ),
                    requestParts(
                        partWithName("multipartFiles").description("?????? ????????? file")
                    ),
                    responseFields(
                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("?????? ????????????"),
                        fieldWithPath("message").type(JsonFieldType.STRING).type(JsonFieldType.NULL).description("?????? ????????? ?????????"),
                        fieldWithPath("data").type(JsonFieldType.VARIES).description("?????? ?????????(?????? ??? ?????? ??????)"),

                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("?????? key"),
                        fieldWithPath("data.title").type(JsonFieldType.STRING).description("?????? ??????"),
                        fieldWithPath("data.category").type(JsonFieldType.VARIES).description("?????? ????????????"),
                        fieldWithPath("data.category.id").type(JsonFieldType.NUMBER).description("???????????? ?????????"),
                        fieldWithPath("data.category.categoryName").type(JsonFieldType.STRING).description("???????????? ??????"),
                        fieldWithPath("data.price").type(JsonFieldType.STRING).description("?????? ??????"),
                        fieldWithPath("data.description").type(JsonFieldType.STRING).description("?????? ??????"),
                        fieldWithPath("data.status").type(JsonFieldType.STRING).description("?????? ??????"),
                        fieldWithPath("data.address").type(JsonFieldType.VARIES).description("?????????"),
                        fieldWithPath("data.address.state").type(JsonFieldType.STRING).description("??????"),
                        fieldWithPath("data.address.city").type(JsonFieldType.STRING).description("?????????"),
                        fieldWithPath("data.address.town").type(JsonFieldType.STRING).description("?????????"),
                        fieldWithPath("data.like").type(JsonFieldType.NUMBER).description("?????? ?????????").optional(),
                        fieldWithPath("data.userId").type(JsonFieldType.NUMBER).description("????????? ?????????").optional(),
                        fieldWithPath("data.images").type(JsonFieldType.VARIES).description("?????? ????????????").optional(),
                        fieldWithPath("data.images.[].id").type(JsonFieldType.NUMBER).description("?????? ????????? key").optional(),
                        fieldWithPath("data.images.[].url").type(JsonFieldType.STRING).description("?????? ????????? url").optional(),
                        fieldWithPath("data.images.[].userId").type(JsonFieldType.NUMBER).description("?????? ???????????? ?????? ?????? key").optional(),
                        fieldWithPath("data.images.[].productId").type(JsonFieldType.NUMBER).description("?????? ???????????? ?????? ?????? key").optional(),
                        fieldWithPath("data.reviews").type(JsonFieldType.VARIES).description("?????? ?????????").optional()
                    )
                ));

    }


    @DisplayName("?????? ????????? ???????????? ?????????")
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
                        parameterWithName("productId").description("?????? key"),
                        parameterWithName("imageId").description("????????? key")
                    ),
                    responseFields(
                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("?????? ????????????"),
                        fieldWithPath("message").type(JsonFieldType.STRING).type(JsonFieldType.NULL).description("?????? ????????? ?????????"),
                        fieldWithPath("data").type(JsonFieldType.VARIES).description("?????? ?????????(?????? ??? ?????? ??????)"),

                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("?????? key"),
                        fieldWithPath("data.title").type(JsonFieldType.STRING).description("?????? ??????"),
                        fieldWithPath("data.category").type(JsonFieldType.VARIES).description("?????? ????????????"),
                        fieldWithPath("data.category.id").type(JsonFieldType.NUMBER).description("???????????? ?????????"),
                        fieldWithPath("data.category.categoryName").type(JsonFieldType.STRING).description("???????????? ??????"),
                        fieldWithPath("data.price").type(JsonFieldType.STRING).description("?????? ??????"),
                        fieldWithPath("data.description").type(JsonFieldType.STRING).description("?????? ??????"),
                        fieldWithPath("data.status").type(JsonFieldType.STRING).description("?????? ??????"),
                        fieldWithPath("data.address").type(JsonFieldType.VARIES).description("?????????"),
                        fieldWithPath("data.address.state").type(JsonFieldType.STRING).description("??????"),
                        fieldWithPath("data.address.city").type(JsonFieldType.STRING).description("?????????"),
                        fieldWithPath("data.address.town").type(JsonFieldType.STRING).description("?????????"),
                        fieldWithPath("data.like").type(JsonFieldType.NUMBER).description("?????? ?????????").optional(),
                        fieldWithPath("data.userId").type(JsonFieldType.NUMBER).description("????????? ?????????").optional(),
                        fieldWithPath("data.images").type(JsonFieldType.VARIES).description("?????? ????????????").optional(),
                        fieldWithPath("data.images.[].id").type(JsonFieldType.NUMBER).description("?????? ????????? key").optional(),
                        fieldWithPath("data.images.[].url").type(JsonFieldType.STRING).description("?????? ????????? url").optional(),
                        fieldWithPath("data.images.[].userId").type(JsonFieldType.NUMBER).description("?????? ???????????? ?????? ?????? key").optional(),
                        fieldWithPath("data.images.[].productId").type(JsonFieldType.NUMBER).description("?????? ???????????? ?????? ?????? key").optional(),
                        fieldWithPath("data.reviews").type(JsonFieldType.VARIES).description("?????? ?????????").optional()
                    )
                ));
    }

    @DisplayName("????????? ?????? ????????????")
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
                        parameterWithName("productId").description("?????? key")
                    ),
                    responseFields(
                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("?????? ????????????"),
                        fieldWithPath("message").type(JsonFieldType.STRING).type(JsonFieldType.NULL).description("?????? ????????? ?????????"),
                        fieldWithPath("data").type(JsonFieldType.VARIES).description("?????? ?????????(?????? ?????????)"),
                        fieldWithPath("data.[].id").type(JsonFieldType.NUMBER).description("?????? key"),
                        fieldWithPath("data.[].content").type(JsonFieldType.STRING).description("?????? ??????"),
                        fieldWithPath("data.[].userId").type(JsonFieldType.NUMBER).description("?????? ????????? key"),
                        fieldWithPath("data.[].productId").type(JsonFieldType.NUMBER).description("?????? ?????? key").optional()
                    )
                ));
    }
}