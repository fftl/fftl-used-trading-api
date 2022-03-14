package fftl.usedtradingapi.user.service;

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
     * 테스트에 필요한 데이터들 셋팅
     */
    @BeforeEach
    void setUp() throws Exception{

        category = Category.builder().id(3L).categoryName("가구/인테리어").build();

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

        product = Product.builder()
            .title("모니터")
            .description("미개봉 모니터 입니당.")
            .category(Category.builder().id(1L).categoryName("디지털기기").build())
            .price("120000")
            .status(Status.SALE)
            .address(Address.builder().state("경기도").city("부천시").town("상동").build())
            .user(user)
            .build();

        multipartFile = new MockMultipartFile("test.xlsx", new FileInputStream(new File("src/main/resources/testImage.jpg")));
        image = Image.builder().imageType(ImageType.User).user(user).url("test.").build();
    }

    @DisplayName("유저 저장하기 테스트")
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

    @DisplayName("유저 로그인 테스트")
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

    @DisplayName("유저 한명 조회하기 테스트")
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

    @DisplayName("유저 정보 수정하기 테스트")
    @Test
    void updateUser() {
        //given
        Long userId = 1L;
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(user));
        SaveUserRequest request = SaveUserRequest.builder().password("5678").categories(categories).state("제주도").city("제주시").town("애월읍").build();

        //when
        User result = userService.updateUser(userId, request);

        //then
        assertEquals(result.getAddress(), user.getAddress());
        assertEquals(result.getPassword(), user.getPassword());
    }

    @DisplayName("유저 상품 찜하기 테스트")
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

    @DisplayName("유저 찜 상품 삭제하기 테스트")
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

    @DisplayName("유저 관심 카테고리 추가하기 테스트")
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

    @DisplayName("유저 관심 카테고리 삭제하기 테스트")
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

    @DisplayName("유저 삭제하기 테스트")
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

    @DisplayName("유저 이미지 추가하기 테스트")
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

    @DisplayName("유저 이미지 삭제하기 테스트")
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