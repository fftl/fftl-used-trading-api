package fftl.usedtradingapi.user.service;

import fftl.usedtradingapi.commons.utils.CategoryService;
import fftl.usedtradingapi.product.service.ProductService;
import fftl.usedtradingapi.user.domain.User;
import fftl.usedtradingapi.user.domain.UserRepository;
import fftl.usedtradingapi.user.dto.LoginUserRequest;
import fftl.usedtradingapi.user.dto.SaveUserRequest;
import fftl.usedtradingapi.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final ProductService productService;
    private final CategoryService categoryService;

    /**
    * 유저 생성하기
    * */
    @Transactional
    public User saveUser(SaveUserRequest request){
        User user = userRepository.save(request.toEntity());
        return user;
    }

    /**
     * 로그인 하기 (password 암호화 작업을 진행해야 합니다.)
     * */
    public User loginUser(LoginUserRequest request){
        //Exception 작업 따로 필요.. 일단 RuntimeException으로 일괄적으로 작업 하겠습니다.
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow(() -> new RuntimeException("올바른 아이디, 비밀번호를 입력해주세요."));

        if( !user.getPassword().equals(request.getPassword()) ){
            throw new RuntimeException("올바른 아이디, 비밀번호를 입력해주세요.");
        }
        return user;
    }

    /**
     * pk로 유저 한명 조회
     * */
    public User getOneUser(Long id){
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("해당 id값을 가진 사용자를 찾을 수 없습니다."));
        return user;
    }

    /**
     * 유저 정보 수정하기
     * */
    public User updateUser(Long id, SaveUserRequest request){
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("해당 id값을 가진 사용자를 찾을 수 없습니다."));
        user.updateUser(request);
        return user;
    }

    /**
     * 찜 상품 추가하기
     * */
    public User addWishProduct(Long userId, Long productId){
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("해당 id값을 가진 사용자를 찾을 수 없습니다."));
        user.addWishProduct(productService.getOneProduct(productId));

        return user;
    }

    /**
     * 찜 상품 삭제하기
     * */
    public User deleteWishProduct(Long userId, Long productId){
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("해당 id값을 가진 사용자를 찾을 수 없습니다."));
        user.deleteWishProduct(productService.getOneProduct(productId));

        return user;
    }

    /**
     * 관심 카테고리 추가하기
     * */
    public User addCategory(Long userId, Long categoryId){
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("해당 id값을 가진 사용자를 찾을 수 없습니다."));
        user.addCategory(categoryService.getOneCategory(categoryId));

        return user;
    }

    /**
     * 관심 카테고리 삭제하기
     * */
    public User deleteDelete(Long userId, Long categoryId){
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("해당 id값을 가진 사용자를 찾을 수 없습니다."));
        user.deleteCategory(categoryService.getOneCategory(categoryId));

        return user;
    }

    /**
     * 유저 삭제 처리
     * */
    public void deleteUser(Long id){
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("해당 id값을 가진 사용자를 찾을 수 없습니다."));
        user.deleteUser();
    }

}
