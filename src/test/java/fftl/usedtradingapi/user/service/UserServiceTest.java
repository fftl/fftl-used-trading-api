package fftl.usedtradingapi.user.service;

import fftl.usedtradingapi.commons.domain.Category;
import fftl.usedtradingapi.commons.utils.CategoryService;
import fftl.usedtradingapi.product.domain.Address;
import fftl.usedtradingapi.user.domain.User;
import fftl.usedtradingapi.user.domain.UserRepository;
import fftl.usedtradingapi.user.dto.LoginUserRequest;
import fftl.usedtradingapi.user.dto.SaveUserRequest;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryService categoryService;

    private User user;
    private SaveUserRequest saveUserRequest;

    private Category category;
    private List<Category> categories;
    private List<Long> categoryIds;

    private LoginUserRequest loginUserRequest;

    @BeforeEach
    void setUp(){

        categories = new ArrayList<>();
        categories.add(Category.builder().id(1L).categoryName("디지털기기").build());
        categories.add(Category.builder().id(2L).categoryName("생활가전").build());

        categoryIds = new ArrayList<>();
        categoryIds.add(1L);
        categoryIds.add(2L);

        user = User.builder()
            .id(1L)
            .username("fftl")
            .password("1234")
            .address(Address.builder().state("경기도").city("부천시").town("상동").build())
            .categories(categories)
            .build();

        saveUserRequest = SaveUserRequest.builder()
            .username("fftl")
            .password("1234")
            .state("경기도")
            .city("부천시")
            .town("상동")
            .categoryIds(categoryIds)
            .build();

        loginUserRequest = LoginUserRequest.builder()
            .username("fftl")
            .password("1234")
            .build();
    }

    @Test
    void saveUser() throws Exception{

        //given
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(categoryService.getOneCategory(any())).thenReturn(Category.builder().id(1L).categoryName("디지털기기").build());

        //when
        User result = userService.saveUser(saveUserRequest);

        //then
        assertEquals(result.getId(), user.getId());
        assertEquals(result.getUsername(), user.getUsername());
        assertEquals(result.getAddress(), user.getAddress());

    }

    @Test
    void loginUser() {
        //given
        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.ofNullable(user));

        //when
        User result = userService.loginUser(loginUserRequest);

        //then
        assertEquals(result.getUsername(), user.getUsername());
        assertEquals(result.getAddress(), user.getAddress());
        assertEquals(result.getId(), user.getId());
    }

    @Test
    void getOneUser() {
    }

    @Test
    void updateUser() {
    }

    @Test
    void addWishProduct() {
    }

    @Test
    void deleteWishProduct() {
    }

    @Test
    void addUserCategory() {
    }

    @Test
    void deleteUserCateogry() {
    }

    @Test
    void deleteUser() {
    }

    @Test
    void addUserImage() {
    }
}