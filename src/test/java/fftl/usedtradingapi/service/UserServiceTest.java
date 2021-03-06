package fftl.usedtradingapi.service;

import fftl.usedtradingapi.commons.domain.Category;
import fftl.usedtradingapi.commons.utils.CategoryService;
import fftl.usedtradingapi.image.domain.Image;
import fftl.usedtradingapi.image.domain.ImageType;
import fftl.usedtradingapi.image.service.ImageService;
import fftl.usedtradingapi.product.domain.Address;
import fftl.usedtradingapi.product.domain.Product;
import fftl.usedtradingapi.product.domain.ProductRepository;
import fftl.usedtradingapi.product.domain.Status;
import fftl.usedtradingapi.user.domain.User;
import fftl.usedtradingapi.user.domain.UserRepository;
import fftl.usedtradingapi.user.dto.LoginUserRequest;
import fftl.usedtradingapi.user.dto.SaveUserRequest;
import fftl.usedtradingapi.user.service.UserService;
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
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private ImageService imageService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    private User user;
    private SaveUserRequest saveUserRequest;

    private Category category;
    private List<Category> categories;
    private List<Long> categoryIds;

    private LoginUserRequest loginUserRequest;
    private Product product;

    private MultipartFile multipartFile;
    private Image image;

    /**
     * ???????????? ????????? ???????????? ??????
     */
    @BeforeEach
    void setUp() throws Exception{

        category = Category.builder().id(3L).categoryName("??????/????????????").build();

        categories = new ArrayList<>();
        categories.add(Category.builder().id(1L).categoryName("???????????????").build());
        categories.add(Category.builder().id(2L).categoryName("????????????").build());

        categoryIds = new ArrayList<>();
        categoryIds.add(1L);
        categoryIds.add(2L);

        user = User.builder()
            .id(1L)
            .username("fftl")
            .password("1234")
            .address(Address.builder().state("?????????").city("?????????").town("??????").build())
            .categories(categories)
            .build();

        saveUserRequest = SaveUserRequest.builder()
            .username("fftl")
            .password("1234")
            .state("?????????")
            .city("?????????")
            .town("??????")
            .categoryIds(categoryIds)
            .build();

        loginUserRequest = LoginUserRequest.builder()
            .username("fftl")
            .password("1234")
            .build();

        product = Product.builder()
            .title("?????????")
            .description("????????? ????????? ?????????.")
            .category(Category.builder().id(1L).categoryName("???????????????").build())
            .price("120000")
            .status(Status.SALE)
            .address(Address.builder().state("?????????").city("?????????").town("??????").build())
            .user(user)
            .build();

        multipartFile = new MockMultipartFile("test.xlsx", new FileInputStream(new File("src/main/resources/testImage.jpg")));
        image = Image.builder().imageType(ImageType.User).user(user).url("test.").build();
    }

    @DisplayName("?????? ???????????? ?????????")
    @Test
    void saveUser() throws Exception{

        //given
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(categoryService.getOneCategory(any())).thenReturn(Category.builder().id(1L).categoryName("???????????????").build());

        //when
        User result = userService.saveUser(saveUserRequest);

        //then
        assertEquals(result.getId(), user.getId());
        assertEquals(result.getUsername(), user.getUsername());
        assertEquals(result.getAddress(), user.getAddress());

    }

    @DisplayName("?????? ????????? ?????????")
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

    @DisplayName("?????? ?????? ???????????? ?????????")
    @Test
    void getOneUser() {
        //given
        Long userId = 1L;
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(user));

        //when
        User result = userService.getOneUser(userId);

        //then
        assertEquals(result.getId(), user.getId());
        assertEquals(result.getUsername(), user.getUsername());
    }

    @DisplayName("?????? ?????? ???????????? ?????????")
    @Test
    void updateUser() {
        //given
        Long userId = 1L;
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(user));
        SaveUserRequest request = SaveUserRequest.builder().password("5678").categories(categories).state("?????????").city("?????????").town("?????????").build();

        //when
        User result = userService.updateUser(userId, request);

        //then
        assertEquals(result.getAddress(), user.getAddress());
        assertEquals(result.getPassword(), user.getPassword());
    }

    @DisplayName("?????? ?????? ????????? ?????????")
    @Test
    void addWishProduct() {
        //given
        Long userId = 1L;
        Long productId = 1L;
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(user));
        when(productRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(product));

        //when
        User result = userService.addWishProduct(1L, 1L);

        //then
        assertEquals(result.getWishProducts(), user.getWishProducts());
        assertEquals(result.getUsername(), user.getUsername());
    }

    @DisplayName("?????? ??? ?????? ???????????? ?????????")
    @Test
    void deleteWishProduct() {
        //given
        Long userId = 1L;
        Long productId = 1L;
        user.addWishProduct(product);
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(user));
        when(productRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(product));

        //when
        User result = userService.deleteWishProduct(1L, 1L);

        //then
        assertEquals(result.getWishProducts(), user.getWishProducts());
        assertEquals(result.getWishProducts().size(), 0);
        assertEquals(result.getUsername(), user.getUsername());
    }

    @DisplayName("?????? ?????? ???????????? ???????????? ?????????")
    @Test
    void addUserCategory() {

        //given
        Long userId = 1L;
        Long categoryId = 1L;
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(user));
        when(categoryService.getOneCategory(any(Long.class))).thenReturn(category);

        //when
        User result = userService.addUserCategory(userId, categoryId);

        //then
        assertEquals(result.getCategories().size(), 3);
        assertEquals(result.getCategories().contains(category), true);
    }

    @DisplayName("?????? ?????? ???????????? ???????????? ?????????")
    @Test
    void deleteUserCategory() {

        //given
        Long userId = 1L;
        Long categoryId = 1L;
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(user));
        when(categoryService.getOneCategory(any(Long.class))).thenReturn(categories.get(1));

        //when
        User result = userService.deleteUserCategory(userId, categoryId);

        //then
        assertEquals(result.getCategories().size(), 1);
        assertEquals(result.getCategories().contains(category), false);

    }

    @DisplayName("?????? ???????????? ?????????")
    @Test
    void deleteUser() {
        //given
        Long userId = 1L;
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(user));

        //when
        userService.deleteUser(userId);

        //then
        assertEquals(user.isRemoved(), true);

    }

    @DisplayName("?????? ????????? ???????????? ?????????")
    @Test
    void addUserImage() throws Exception{
        //given
        Long userId = 1L;
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(user));
        when(imageService.uploadUserImage(any(MultipartFile.class), any(Long.class))).thenReturn(image);

        //when
        User result = userService.addUserImage(userId, multipartFile);

        //then
        assertEquals(result.getImage(), image);
        assertEquals(result.getImage().getUrl(), image.getUrl());

    }

    @DisplayName("?????? ????????? ???????????? ?????????")
    @Test
    void deleteUserImage() throws Exception{
        //given
        user.addUserImage(image);
        Long userId = 1L;
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(user));

        //when
        User result = userService.deleteUserImage(userId);

        //then
        assertEquals(result.getImage(), null);
    }
}