package fftl.usedtradingapi.controller;

import fftl.usedtradingapi.commons.domain.Category;
import fftl.usedtradingapi.product.domain.Address;
import fftl.usedtradingapi.user.controller.UserController;
import fftl.usedtradingapi.user.domain.User;
import fftl.usedtradingapi.user.dto.SaveUserRequest;
import fftl.usedtradingapi.user.service.UserService;
import org.hibernate.boot.cfgxml.spi.MappingReference;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.junit.jupiter.api.Assertions.*;
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
    List<Long> categoryIds;
    SaveUserRequest saveUserRequest;
    User user;

    @BeforeEach
    void setUp() {
        categories = new ArrayList<>();
        categories.add(Category.builder().id(1L).categoryName("디지털/가전").build());
        categories.add(Category.builder().id(2L).categoryName("의류").build());

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

    }

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

    @Test
    void loginUser() {
    }

    @Test
    void getOneUser() {
    }

    @Test
    void updateUser() {
    }

    @Test
    void deleteUser() {
    }

    @Test
    void addUserCategory() {
    }

    @Test
    void deleteCategory() {
    }

    @Test
    void addWishProduct() {
    }

    @Test
    void deleteWishProduct() {
    }

    @Test
    void addUserImage() {
    }

    @Test
    void deleteUserImage() {
    }
}