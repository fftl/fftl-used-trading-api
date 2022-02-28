package fftl.usedtradingapi.user.service;

import fftl.usedtradingapi.user.domain.User;
import fftl.usedtradingapi.user.domain.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Test
    void saveUser() {
        when(userRepository.save(any(User.class))).thenReturn("a");
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