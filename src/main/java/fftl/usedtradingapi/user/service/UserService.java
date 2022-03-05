package fftl.usedtradingapi.user.service;

import fftl.usedtradingapi.commons.domain.Category;
import fftl.usedtradingapi.commons.utils.CategoryService;
import fftl.usedtradingapi.image.domain.Image;
import fftl.usedtradingapi.image.service.ImageService;
import fftl.usedtradingapi.product.domain.ProductRepository;
import fftl.usedtradingapi.product.service.ProductService;
import fftl.usedtradingapi.user.domain.User;
import fftl.usedtradingapi.user.domain.UserRepository;
import fftl.usedtradingapi.user.dto.LoginUserRequest;
import fftl.usedtradingapi.user.dto.SaveUserRequest;
import fftl.usedtradingapi.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final ImageService imageService;

    /**
    * 유저 생성하기
    * */
    @Transactional
    public User saveUser(SaveUserRequest request) throws IOException {

        //유저의 객체를 일단 생성
        User user = userRepository.save(request.toEntity());

        //만약 이미지 파일이 들어 있다면 이미지를 추가
        if(request.getMultipartFile() != null){
            Image image = imageService.uploadUserImage(request.getMultipartFile(), user.getId());
            user.addUserImage(image);
        }

        //카테고리를 추가
        for(Long categoryId : request.getCategoryIds()){
            Category category = categoryService.getOneCategory(categoryId);
            user.addCategory(category);
        }

        return user;
    }

    /**
     * 로그인 하기
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
    public User getOneUser(Long userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("해당 아이디 가진 유저는 존재하지 않습니다."));
        return user;
    }

    /**
     * 유저 정보 수정하기
     * */
    public User updateUser(Long userId, SaveUserRequest request){
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("해당 id값을 가진 사용자를 찾을 수 없습니다."));
        user.updateUser(request);
        return user;
    }

    /**
     * 찜 상품 추가하기
     * */
    public User addWishProduct(Long userId, Long productId){
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("해당 아이디 가진 유저는 존재하지 않습니다."));
        user.addWishProduct(productRepository.findById(productId).orElseThrow(()-> new RuntimeException("해당 아이디를 가진 상품은 존재하지 않습니다.")));

        return user;
    }

    /**
     * 찜 상품 삭제하기
     * */
    public User deleteWishProduct(Long userId, Long productId){
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("해당 아이디 가진 유저는 존재하지 않습니다."));
        user.deleteWishProduct(productRepository.findById(productId).orElseThrow(()-> new RuntimeException("해당 아이디를 가진 상품은 존재하지 않습니다.")));

        return user;
    }

    /**
     * 관심 카테고리 추가하기
     * */
    public User addUserCategory(Long userId, Long categoryId){
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("해당 아이디 가진 유저는 존재하지 않습니다."));
        user.addCategory(categoryService.getOneCategory(categoryId));

        return user;
    }

    /**
     * 관심 카테고리 삭제하기
     * */
    public User deleteUserCateogry(Long userId, Long categoryId){
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("해당 아이디 가진 유저는 존재하지 않습니다."));
        user.deleteCategory(categoryService.getOneCategory(categoryId));

        return user;
    }

    /**
     * 유저 삭제 처리
     * */
    public void deleteUser(Long userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("해당 아이디 가진 유저는 존재하지 않습니다."));
        user.deleteUser();
    }

    /**
     * 유저 이미지 추가(유저가 생성된 상태이고, Image가 비어있을 때)
     * */
    public User addUserImage(Long userId, MultipartFile multipartFile) throws IOException{
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("해당 아이디 가진 유저는 존재하지 않습니다."));
        if( user.getImage() != null ){
            throw new RuntimeException("유저는 이미지를 하나만 등록할 수 있습니다.");
        }

        Image image = imageService.uploadUserImage(multipartFile, user.getId());

        user.addUserImage(image);

        return user;
    }

    /**
     * 유저 이미지 삭제
     * */
    public User deleteUserImage(Long userId) throws IOException{
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("해당 아이디 가진 유저는 존재하지 않습니다."));
        if( user.getImage() == null ) {
            throw new RuntimeException("등록된 이미지가 없습니다.");
        }

        user.deleteUserImage();

        return user;
    }
}
