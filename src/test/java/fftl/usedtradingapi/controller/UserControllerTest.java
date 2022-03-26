package fftl.usedtradingapi.controller;

import fftl.usedtradingapi.commons.domain.Category;
import fftl.usedtradingapi.product.domain.Address;
import fftl.usedtradingapi.user.controller.UserController;
import fftl.usedtradingapi.user.domain.User;
import fftl.usedtradingapi.user.dto.LoginUserRequest;
import fftl.usedtradingapi.user.dto.SaveUserRequest;
import fftl.usedtradingapi.user.service.UserService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.DomainEvents;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureRestDocs
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    List<Category> categories;
    List<Category> updateCategories;

    List<Long> categoryIds;

    SaveUserRequest saveUserRequest;

    User user;
    User updateUser;


    @BeforeEach
    void setUp() {
        categories = new ArrayList<>();
        categories.add(Category.builder().id(1L).categoryName("디지털/가전").build());
        categories.add(Category.builder().id(2L).categoryName("의류").build());

        updateCategories = new ArrayList<>();
        updateCategories.add(Category.builder().id(3L).categoryName("식물").build());
        updateCategories.add(Category.builder().id(4L).categoryName("반려동물 용품").build());

        categoryIds = new ArrayList<>();
        categoryIds.add(1L);
        categoryIds.add(2L);

        saveUserRequest = SaveUserRequest.
            builder()
            .username("fftl")
            .password("123123")
            .state("경기도")
            .city("부천시")
            .town("상동")
            .categoryIds(categoryIds)
            .build();

        user = User.builder()
            .id(1L)
            .username("fftl")
            .password("123123")
            .address(Address.builder().state("경기도").city("부천시").town("상동").build())
            .categories(categories)
            .removed(false)
            .build();

        updateUser = User.builder()
            .id(1L)
            .username("fftl")
            .password("456456")
            .address(Address.builder().state("서울시").city("중구").town("금천동").build())
            .categories(updateCategories)
            .removed(false)
            .build();

    }

    @DisplayName("유저 저장 테스트")
    @Test
    void saveUser() throws Exception{
        //give
        JSONArray jsonCategoryIds = new JSONArray();
        jsonCategoryIds.put(1L);
        jsonCategoryIds.put(2L);

        when(userService.saveUser(any(SaveUserRequest.class))).thenReturn(user);
        JSONObject object = new JSONObject()
            .put("username", "fftl")
            .put("password", "123123")
            .put("state", "경기도")
            .put("city", "부천시")
            .put("town", "상동")
            .put("categoryIds", jsonCategoryIds);

        //when
        ResultActions actions = mockMvc.perform(
            post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(String.valueOf(object)));

        //then
        actions
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").doesNotExist())
            .andDo(document("saveUser",
                    requestFields(
                        fieldWithPath("username").type(JsonFieldType.STRING).description("유저 아이디"),
                        fieldWithPath("password").type(JsonFieldType.STRING).description("유저 비밀번호"),
                        fieldWithPath("categoryIds").type(JsonFieldType.ARRAY).description("선호 카테고리들"),
                        fieldWithPath("state").type(JsonFieldType.STRING).description("거주지(도)"),
                        fieldWithPath("city").type(JsonFieldType.STRING).description("거주지(시군구)"),
                        fieldWithPath("town").type(JsonFieldType.STRING).description("거주지(읍면동)"),
                        fieldWithPath("multipartFile").type(JsonFieldType.VARIES).description("프로필 이미지").optional()
                    ),
                    responseFields(
                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공여부"),
                        fieldWithPath("message").type(JsonFieldType.STRING).type(JsonFieldType.NULL).description("요청 실패시 메시지"),
                        fieldWithPath("data").type(JsonFieldType.VARIES).description("요청 데이터(가입 된 유저 정보)"),

                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("유저 key"),
                        fieldWithPath("data.username").type(JsonFieldType.STRING).description("유저 아이디"),
                        fieldWithPath("data.categories").type(JsonFieldType.VARIES).description("선호 카테고리들"),
                        fieldWithPath("data.categories.[].id").type(JsonFieldType.VARIES).description("카테고리 아이디"),
                        fieldWithPath("data.categories.[].categoryName").type(JsonFieldType.VARIES).description("카테고리 이름"),
                        fieldWithPath("data.address").type(JsonFieldType.VARIES).description("거주지"),
                        fieldWithPath("data.address.state").type(JsonFieldType.VARIES).description("시도"),
                        fieldWithPath("data.address.city").type(JsonFieldType.VARIES).description("시군구"),
                        fieldWithPath("data.address.town").type(JsonFieldType.VARIES).description("음면동"),
                        fieldWithPath("data.myProducts").type(JsonFieldType.VARIES).description("내가 등록한 상품들").optional(),
                        fieldWithPath("data.wishProducts").type(JsonFieldType.VARIES).description("찜한 상품들").optional(),
                        fieldWithPath("data.reviews").type(JsonFieldType.VARIES).description("거래 후기").optional(),
                        fieldWithPath("data.image").type(JsonFieldType.STRING).description("프로필 이미지").optional()
                    )
                ));
    }

    @DisplayName("유저 로그인 테스트")
    @Test
    void loginUser() throws Exception{
        //given
        when(userService.loginUser(any(LoginUserRequest.class))).thenReturn(user);
        JSONObject object = new JSONObject()
            .put("username", "fftl")
            .put("password", "123123");

        //when
        ResultActions actions = mockMvc.perform(
            post("/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(String.valueOf(object)));

        //then
        actions.andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").doesNotExist())
            .andDo(
                document("loginUser",
                    requestFields(
                        fieldWithPath("username").type(JsonFieldType.STRING).description("유저 아이디"),
                        fieldWithPath("password").type(JsonFieldType.STRING).description("유저 비밀번호")
                    ),
                    responseFields(
                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공여부"),
                        fieldWithPath("message").type(JsonFieldType.STRING).type(JsonFieldType.NULL).description("요청 실패시 메시지"),
                        fieldWithPath("data").type(JsonFieldType.VARIES).description("요청 데이터(로그인 된 유저 정보)"),

                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("유저 key"),
                        fieldWithPath("data.username").type(JsonFieldType.STRING).description("유저 아이디"),
                        fieldWithPath("data.categories").type(JsonFieldType.VARIES).description("선호 카테고리들"),
                        fieldWithPath("data.categories.[].id").type(JsonFieldType.VARIES).description("카테고리 아이디"),
                        fieldWithPath("data.categories.[].categoryName").type(JsonFieldType.VARIES).description("카테고리 이름"),
                        fieldWithPath("data.address").type(JsonFieldType.VARIES).description("거주지"),
                        fieldWithPath("data.address.state").type(JsonFieldType.VARIES).description("시도"),
                        fieldWithPath("data.address.city").type(JsonFieldType.VARIES).description("시군구"),
                        fieldWithPath("data.address.town").type(JsonFieldType.VARIES).description("음면동"),
                        fieldWithPath("data.myProducts").type(JsonFieldType.VARIES).description("내가 등록한 상품들").optional(),
                        fieldWithPath("data.wishProducts").type(JsonFieldType.VARIES).description("찜한 상품들").optional(),
                        fieldWithPath("data.reviews").type(JsonFieldType.VARIES).description("거래 후기").optional(),
                        fieldWithPath("data.image").type(JsonFieldType.STRING).description("프로필 이미지").optional()
                    )

                )
            );
    }

    @DisplayName("유저 한명 정보 조회하기 테스트")
    @Test
    void getOneUser() throws Exception{
        //given
        when(userService.getOneUser(any(Long.class))).thenReturn(user);

        //when
        ResultActions actions = mockMvc.perform(
            //url에 파라미터를 넣어서 요청을 하는 경우에는 RestDocumentationRequestBuilders를 이용하며 아래와 같이 주소를 입력
            RestDocumentationRequestBuilders.get("/user/{userId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8"));

        //then
        actions.andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").doesNotExist())
            .andDo(
                document("getOneUser",
                    //url의 파라미터는 아래와 같이 입력할 수 있다.
                    pathParameters(
                            parameterWithName("userId").description("유저 key")
                    ),
                    responseFields(
                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공여부"),
                        fieldWithPath("message").type(JsonFieldType.STRING).type(JsonFieldType.NULL).description("요청 실패시 메시지"),
                        fieldWithPath("data").type(JsonFieldType.VARIES).description("요청 데이터(로그인 된 유저 정보)"),

                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("유저 key"),
                        fieldWithPath("data.username").type(JsonFieldType.STRING).description("유저 아이디"),
                        fieldWithPath("data.categories").type(JsonFieldType.VARIES).description("선호 카테고리들"),
                        fieldWithPath("data.categories.[].id").type(JsonFieldType.VARIES).description("카테고리 아이디"),
                        fieldWithPath("data.categories.[].categoryName").type(JsonFieldType.VARIES).description("카테고리 이름"),
                        fieldWithPath("data.address").type(JsonFieldType.VARIES).description("거주지"),
                        fieldWithPath("data.address.state").type(JsonFieldType.VARIES).description("시도"),
                        fieldWithPath("data.address.city").type(JsonFieldType.VARIES).description("시군구"),
                        fieldWithPath("data.address.town").type(JsonFieldType.VARIES).description("음면동"),
                        fieldWithPath("data.myProducts").type(JsonFieldType.VARIES).description("내가 등록한 상품들").optional(),
                        fieldWithPath("data.wishProducts").type(JsonFieldType.VARIES).description("찜한 상품들").optional(),
                        fieldWithPath("data.reviews").type(JsonFieldType.VARIES).description("거래 후기").optional(),
                        fieldWithPath("data.image").type(JsonFieldType.STRING).description("프로필 이미지").optional()
                    )

                )
            );
    }

    @DisplayName("유저 정보 변경하기 테스트")
    @Test
    void updateUser() throws Exception{
        //give
        when(userService.updateUser(any(Long.class), any(SaveUserRequest.class))).thenReturn(updateUser);

        JSONArray jsonCategoryIds = new JSONArray();
        jsonCategoryIds.put(1L);
        jsonCategoryIds.put(2L);

        JSONObject object = new JSONObject()
            .put("username", "fftl")
            .put("password", "456456")
            .put("state", "서울시")
            .put("city", "중구")
            .put("town", "금천동")
            .put("categoryIds", jsonCategoryIds);

        //when
        ResultActions actions = mockMvc.perform(
            RestDocumentationRequestBuilders.patch("/user/{userId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(String.valueOf(object)));

        //then
        actions.andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").doesNotExist())
            .andExpect(jsonPath("$.data.address.state").value("서울시"))
            .andExpect(jsonPath("$.data.categories.[0].categoryName").value("식물"))
            .andDo(
                document("updateUser",
                    //url의 파라미터는 아래와 같이 입력할 수 있다.
                    pathParameters(
                            parameterWithName("userId").description("유저 key")
                    ),
                    responseFields(
                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공여부"),
                        fieldWithPath("message").type(JsonFieldType.STRING).type(JsonFieldType.NULL).description("요청 실패시 메시지"),
                        fieldWithPath("data").type(JsonFieldType.VARIES).description("요청 데이터(로그인 된 유저 정보)"),

                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("유저 key"),
                        fieldWithPath("data.username").type(JsonFieldType.STRING).description("유저 아이디"),
                        fieldWithPath("data.categories").type(JsonFieldType.VARIES).description("선호 카테고리들"),
                        fieldWithPath("data.categories.[].id").type(JsonFieldType.VARIES).description("카테고리 아이디"),
                        fieldWithPath("data.categories.[].categoryName").type(JsonFieldType.VARIES).description("카테고리 이름"),
                        fieldWithPath("data.address").type(JsonFieldType.VARIES).description("거주지"),
                        fieldWithPath("data.address.state").type(JsonFieldType.VARIES).description("시도"),
                        fieldWithPath("data.address.city").type(JsonFieldType.VARIES).description("시군구"),
                        fieldWithPath("data.address.town").type(JsonFieldType.VARIES).description("음면동"),
                        fieldWithPath("data.myProducts").type(JsonFieldType.VARIES).description("내가 등록한 상품들").optional(),
                        fieldWithPath("data.wishProducts").type(JsonFieldType.VARIES).description("찜한 상품들").optional(),
                        fieldWithPath("data.reviews").type(JsonFieldType.VARIES).description("거래 후기").optional(),
                        fieldWithPath("data.image").type(JsonFieldType.STRING).description("프로필 이미지").optional()
                    )

                )
            );

    }

    @Test
    void deleteUser() {
        //given
        //when
        //then
    }

    @DisplayName("유저 카테고리 추가하기 테스트")
    @Test
    void addUserCategory() throws Exception{
        //given
        when(userService.addUserCategory(any(Long.class), any(Long.class))).thenReturn(user);

        //when
        ResultActions actions = mockMvc.perform(
            RestDocumentationRequestBuilders.post("/user/category/{userId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .param("categoryId", String.valueOf(2L)));

        //then
        actions.andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").doesNotExist())
            .andDo(
                document("addUserCategory",
                    //url의 파라미터는 아래와 같이 입력할 수 있다.
                    pathParameters(
                            parameterWithName("userId").description("유저 key")
                    ),
                    responseFields(
                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공여부"),
                        fieldWithPath("message").type(JsonFieldType.STRING).type(JsonFieldType.NULL).description("요청 실패시 메시지"),
                        fieldWithPath("data").type(JsonFieldType.VARIES).description("요청 데이터(로그인 된 유저 정보)"),

                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("유저 key"),
                        fieldWithPath("data.username").type(JsonFieldType.STRING).description("유저 아이디"),
                        fieldWithPath("data.categories").type(JsonFieldType.VARIES).description("선호 카테고리들"),
                        fieldWithPath("data.categories.[].id").type(JsonFieldType.VARIES).description("카테고리 아이디"),
                        fieldWithPath("data.categories.[].categoryName").type(JsonFieldType.VARIES).description("카테고리 이름"),
                        fieldWithPath("data.address").type(JsonFieldType.VARIES).description("거주지"),
                        fieldWithPath("data.address.state").type(JsonFieldType.VARIES).description("시도"),
                        fieldWithPath("data.address.city").type(JsonFieldType.VARIES).description("시군구"),
                        fieldWithPath("data.address.town").type(JsonFieldType.VARIES).description("음면동"),
                        fieldWithPath("data.myProducts").type(JsonFieldType.VARIES).description("내가 등록한 상품들").optional(),
                        fieldWithPath("data.wishProducts").type(JsonFieldType.VARIES).description("찜한 상품들").optional(),
                        fieldWithPath("data.reviews").type(JsonFieldType.VARIES).description("거래 후기").optional(),
                        fieldWithPath("data.image").type(JsonFieldType.STRING).description("프로필 이미지").optional()
                    )

                )
            );
    }

    @DisplayName("선호 카테고리 삭제 테스트")
    @Test
    void deleteCategory() throws Exception{
        //given
        when(userService.deleteUserCategory(any(Long.class), any(Long.class))).thenReturn(user);

        //when
        ResultActions actions = mockMvc.perform(
            RestDocumentationRequestBuilders.delete("/user/category/{userId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .param("categoryId", String.valueOf(2L)));

        //then
        actions.andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").doesNotExist())
            .andDo(
                document("deleteCategory",
                    //url의 파라미터는 아래와 같이 입력할 수 있다.
                    pathParameters(
                            parameterWithName("userId").description("유저 key")
                    ),
                    responseFields(
                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공여부"),
                        fieldWithPath("message").type(JsonFieldType.STRING).type(JsonFieldType.NULL).description("요청 실패시 메시지"),
                        fieldWithPath("data").type(JsonFieldType.VARIES).description("요청 데이터(로그인 된 유저 정보)"),

                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("유저 key"),
                        fieldWithPath("data.username").type(JsonFieldType.STRING).description("유저 아이디"),
                        fieldWithPath("data.categories").type(JsonFieldType.VARIES).description("선호 카테고리들"),
                        fieldWithPath("data.categories.[].id").type(JsonFieldType.VARIES).description("카테고리 아이디"),
                        fieldWithPath("data.categories.[].categoryName").type(JsonFieldType.VARIES).description("카테고리 이름"),
                        fieldWithPath("data.address").type(JsonFieldType.VARIES).description("거주지"),
                        fieldWithPath("data.address.state").type(JsonFieldType.VARIES).description("시도"),
                        fieldWithPath("data.address.city").type(JsonFieldType.VARIES).description("시군구"),
                        fieldWithPath("data.address.town").type(JsonFieldType.VARIES).description("음면동"),
                        fieldWithPath("data.myProducts").type(JsonFieldType.VARIES).description("내가 등록한 상품들").optional(),
                        fieldWithPath("data.wishProducts").type(JsonFieldType.VARIES).description("찜한 상품들").optional(),
                        fieldWithPath("data.reviews").type(JsonFieldType.VARIES).description("거래 후기").optional(),
                        fieldWithPath("data.image").type(JsonFieldType.STRING).description("프로필 이미지").optional()
                    )

                )
            );
    }

    @DisplayName("찜한 상품 추가하기 테스트")
    @Test
    void addWishProduct() throws Exception{
        //given
        when(userService.addWishProduct(any(Long.class), any(Long.class))).thenReturn(user);

        //when
        ResultActions actions = mockMvc.perform(
            RestDocumentationRequestBuilders.post("/user/wishProduct/{userId}/{productId}",1L, 2L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8"));

        //then
        actions.andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").doesNotExist())
            .andDo(
                document("addWishProduct",
                    //url의 파라미터는 아래와 같이 입력할 수 있다.
                    pathParameters(
                            parameterWithName("userId").description("유저 key"),
                        parameterWithName("productId").description("찜 한 상품 key")
                    ),
                    responseFields(
                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공여부"),
                        fieldWithPath("message").type(JsonFieldType.STRING).type(JsonFieldType.NULL).description("요청 실패시 메시지"),
                        fieldWithPath("data").type(JsonFieldType.VARIES).description("요청 데이터(로그인 된 유저 정보)"),

                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("유저 key"),
                        fieldWithPath("data.username").type(JsonFieldType.STRING).description("유저 아이디"),
                        fieldWithPath("data.categories").type(JsonFieldType.VARIES).description("선호 카테고리들"),
                        fieldWithPath("data.categories.[].id").type(JsonFieldType.VARIES).description("카테고리 아이디"),
                        fieldWithPath("data.categories.[].categoryName").type(JsonFieldType.VARIES).description("카테고리 이름"),
                        fieldWithPath("data.address").type(JsonFieldType.VARIES).description("거주지"),
                        fieldWithPath("data.address.state").type(JsonFieldType.VARIES).description("시도"),
                        fieldWithPath("data.address.city").type(JsonFieldType.VARIES).description("시군구"),
                        fieldWithPath("data.address.town").type(JsonFieldType.VARIES).description("음면동"),
                        fieldWithPath("data.myProducts").type(JsonFieldType.VARIES).description("내가 등록한 상품들").optional(),
                        fieldWithPath("data.wishProducts").type(JsonFieldType.VARIES).description("찜한 상품들").optional(),
                        fieldWithPath("data.reviews").type(JsonFieldType.VARIES).description("거래 후기").optional(),
                        fieldWithPath("data.image").type(JsonFieldType.STRING).description("프로필 이미지").optional()
                    )

                )
            );
    }

    @DisplayName("찜한 상품 제거하기 테스트")
    @Test
    void deleteWishProduct() throws Exception{
        //given
        when(userService.deleteWishProduct(any(Long.class), any(Long.class))).thenReturn(user);

        //when
        ResultActions actions = mockMvc.perform(
            RestDocumentationRequestBuilders.delete("/user/wishProduct/{userId}/{productId}",1L, 2L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8"));

        //then
        actions.andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").doesNotExist())
            .andDo(
                document("addWishProduct",
                    //url의 파라미터는 아래와 같이 입력할 수 있다.
                    pathParameters(
                            parameterWithName("userId").description("유저 key"),
                        parameterWithName("productId").description("찜 한 상품 key")
                    ),
                    responseFields(
                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공여부"),
                        fieldWithPath("message").type(JsonFieldType.STRING).type(JsonFieldType.NULL).description("요청 실패시 메시지"),
                        fieldWithPath("data").type(JsonFieldType.VARIES).description("요청 데이터(로그인 된 유저 정보)"),

                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("유저 key"),
                        fieldWithPath("data.username").type(JsonFieldType.STRING).description("유저 아이디"),
                        fieldWithPath("data.categories").type(JsonFieldType.VARIES).description("선호 카테고리들"),
                        fieldWithPath("data.categories.[].id").type(JsonFieldType.VARIES).description("카테고리 아이디"),
                        fieldWithPath("data.categories.[].categoryName").type(JsonFieldType.VARIES).description("카테고리 이름"),
                        fieldWithPath("data.address").type(JsonFieldType.VARIES).description("거주지"),
                        fieldWithPath("data.address.state").type(JsonFieldType.VARIES).description("시도"),
                        fieldWithPath("data.address.city").type(JsonFieldType.VARIES).description("시군구"),
                        fieldWithPath("data.address.town").type(JsonFieldType.VARIES).description("음면동"),
                        fieldWithPath("data.myProducts").type(JsonFieldType.VARIES).description("내가 등록한 상품들").optional(),
                        fieldWithPath("data.wishProducts").type(JsonFieldType.VARIES).description("찜한 상품들").optional(),
                        fieldWithPath("data.reviews").type(JsonFieldType.VARIES).description("거래 후기").optional(),
                        fieldWithPath("data.image").type(JsonFieldType.STRING).description("프로필 이미지").optional()
                    )

                )
            );
    }

    @DisplayName("유저 이미지 추가하기 테스트")
    @Test
    void addUserImage() throws Exception{
        //given
        when(userService.addUserImage(any(Long.class), any(MultipartFile.class))).thenReturn(user);
        MockMultipartFile imageFile = new MockMultipartFile("multipartFile", new FileInputStream(new File("src/main/resources/testImage.jpg")));

        //when
        ResultActions actions = mockMvc.perform(
            RestDocumentationRequestBuilders.multipart("/user/image/{userId}", 1L)
                .file(imageFile)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8"));

        //then
        actions.andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").doesNotExist())
            .andDo(
                document("addUserImage",
                    //url의 파라미터는 아래와 같이 입력할 수 있다.
                    pathParameters(
                            parameterWithName("userId").description("유저 key")
                    ),
                    requestParts(
                        partWithName("multipartFile").description("유저 이미지 파일")
                    ),
                    responseFields(
                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공여부"),
                        fieldWithPath("message").type(JsonFieldType.STRING).type(JsonFieldType.NULL).description("요청 실패시 메시지"),
                        fieldWithPath("data").type(JsonFieldType.VARIES).description("요청 데이터(로그인 된 유저 정보)"),

                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("유저 key"),
                        fieldWithPath("data.username").type(JsonFieldType.STRING).description("유저 아이디"),
                        fieldWithPath("data.categories").type(JsonFieldType.VARIES).description("선호 카테고리들"),
                        fieldWithPath("data.categories.[].id").type(JsonFieldType.VARIES).description("카테고리 아이디"),
                        fieldWithPath("data.categories.[].categoryName").type(JsonFieldType.VARIES).description("카테고리 이름"),
                        fieldWithPath("data.address").type(JsonFieldType.VARIES).description("거주지"),
                        fieldWithPath("data.address.state").type(JsonFieldType.VARIES).description("시도"),
                        fieldWithPath("data.address.city").type(JsonFieldType.VARIES).description("시군구"),
                        fieldWithPath("data.address.town").type(JsonFieldType.VARIES).description("음면동"),
                        fieldWithPath("data.myProducts").type(JsonFieldType.VARIES).description("내가 등록한 상품들").optional(),
                        fieldWithPath("data.wishProducts").type(JsonFieldType.VARIES).description("찜한 상품들").optional(),
                        fieldWithPath("data.reviews").type(JsonFieldType.VARIES).description("거래 후기").optional(),
                        fieldWithPath("data.image").type(JsonFieldType.STRING).description("프로필 이미지").optional()
                    )

                )
            );
    }

    @DisplayName("유저 이미지 파일 삭제 테스트")
    @Test
    void deleteUserImage() throws Exception{
        //given
        when(userService.deleteUserImage(any(Long.class))).thenReturn(user);

        //when
        ResultActions actions = mockMvc.perform(
            RestDocumentationRequestBuilders.delete("/user/image/{userId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8"));

        //then
        actions.andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").doesNotExist())
            .andDo(
                document("deleteUserImage",
                    //url의 파라미터는 아래와 같이 입력할 수 있다.
                    pathParameters(
                            parameterWithName("userId").description("유저 key")
                    ),
                    responseFields(
                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공여부"),
                        fieldWithPath("message").type(JsonFieldType.STRING).type(JsonFieldType.NULL).description("요청 실패시 메시지"),
                        fieldWithPath("data").type(JsonFieldType.VARIES).description("요청 데이터(로그인 된 유저 정보)"),

                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("유저 key"),
                        fieldWithPath("data.username").type(JsonFieldType.STRING).description("유저 아이디"),
                        fieldWithPath("data.categories").type(JsonFieldType.VARIES).description("선호 카테고리들"),
                        fieldWithPath("data.categories.[].id").type(JsonFieldType.VARIES).description("카테고리 아이디"),
                        fieldWithPath("data.categories.[].categoryName").type(JsonFieldType.VARIES).description("카테고리 이름"),
                        fieldWithPath("data.address").type(JsonFieldType.VARIES).description("거주지"),
                        fieldWithPath("data.address.state").type(JsonFieldType.VARIES).description("시도"),
                        fieldWithPath("data.address.city").type(JsonFieldType.VARIES).description("시군구"),
                        fieldWithPath("data.address.town").type(JsonFieldType.VARIES).description("음면동"),
                        fieldWithPath("data.myProducts").type(JsonFieldType.VARIES).description("내가 등록한 상품들").optional(),
                        fieldWithPath("data.wishProducts").type(JsonFieldType.VARIES).description("찜한 상품들").optional(),
                        fieldWithPath("data.reviews").type(JsonFieldType.VARIES).description("거래 후기").optional(),
                        fieldWithPath("data.image").type(JsonFieldType.STRING).description("프로필 이미지").optional()
                    )

                )
            );
    }
}