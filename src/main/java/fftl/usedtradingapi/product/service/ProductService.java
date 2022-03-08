package fftl.usedtradingapi.product.service;

import fftl.usedtradingapi.commons.domain.Category;
import fftl.usedtradingapi.commons.domain.CategoryRepository;
import fftl.usedtradingapi.image.domain.Image;
import fftl.usedtradingapi.image.service.ImageService;
import fftl.usedtradingapi.product.domain.Product;
import fftl.usedtradingapi.product.domain.ProductRepository;
import fftl.usedtradingapi.product.domain.Status;
import fftl.usedtradingapi.product.dto.SaveProductRequest;
import fftl.usedtradingapi.review.domain.Review;
import fftl.usedtradingapi.user.domain.User;
import fftl.usedtradingapi.user.domain.UserRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ImageService imageService;

    @Transactional
    public Product saveProduct(SaveProductRequest saveProductRequest) throws IOException {

        // User Entity 가져오기
        User user = userRepository.findById(saveProductRequest.getUserId()).orElseThrow(() -> new RuntimeException("해당 아이디를 가진 유저는 존재하지 않습니다."));
        saveProductRequest.setUser(user);

        // Category Entity 가져오기
        Category category = categoryRepository.findById(saveProductRequest.getCategoryId()).orElseThrow(() -> new RuntimeException("해당 아이디를 가진 카테고리는 존재하지 않습니다."));
        saveProductRequest.setCategory(category);

        // Product 등록하고 ProductId를 가져옴
        // 그리고 ProductId 에 image를 등록합니다.
        Product product = productRepository.save(saveProductRequest.toEntity());
        List<Image> images = imageService.uploadProductImage(saveProductRequest.getFiles(), product.getId());

        // 등록한 image들을 가지고 product에 다시 등록
        product.uploadProductImage(images);

        return product;
    }

    @Transactional
    public Product updateProduct(Long productId, SaveProductRequest saveProductRequest) throws IOException {

        // Category Entity 가져오기
        Category category = categoryRepository.findById(saveProductRequest.getCategoryId()).orElseThrow(() -> new RuntimeException("해당 아이디를 가진 카테고리는 존재하지 않습니다."));
        saveProductRequest.setCategory(category);

        // User Entity 가져오기
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("해당 아이디를 가진 상품이 존재하지 않습니다."));
        product.updateProduct(saveProductRequest);

        return product;
    }

    /**
     * 상품 조회
     * */
    public List<Product> getAllProduct(){
        List<Product> products = findSaleStatusProducts(productRepository.findAll());
        return products;
    }

    public Product getOneProduct(Long productId){
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("해당 아이디를 가진 상품이 존재하지 않습니다."));
        return product;
    }

    /**
     * 지역별 상품 조회
     * */
    public List<Product> getProductByState(String state) {
        List<Product> products = findSaleStatusProducts(productRepository.findByAddressState(state));
        return products;
    }

    public List<Product> getProductByCity(String city) {
        List<Product> products = findSaleStatusProducts(productRepository.findByAddressCity(city));
        return products;
    }

    public List<Product> getProductByTown(String town) {
        List<Product> products = findSaleStatusProducts(productRepository.findByAddressTown(town));
        return products;
    }

    /**
     * 상품 상태 변경
     * */
    // 판매중 처리
    @Transactional
    public void  saleProduct(Long productId){
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("해당 아이디를 가진 상품이 존재하지 않습니다."));
        product.statusSale();
    }

    // 판매완료 처리
    @Transactional
    public void  completeProduct(Long productId){
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("해당 아이디를 가진 상품이 존재하지 않습니다."));
        product.statusComplete();
    }

    // 주문취소 처리
    @Transactional
    public void  cancelProduct(Long productId){
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("해당 아이디를 가진 상품이 존재하지 않습니다."));
        product.statusCancel();
    }

    /**
     * 좋아요 증가 감소
     * */

    // 좋아요 증가
    @Transactional
    public void plusLike(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("해당 아이디를 가진 상품이 존재하지 않습니다."));
        product.plusLike();
    }

    // 좋아요 감소
    @Transactional
    public void minusLike(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("해당 아이디를 가진 상품이 존재하지 않습니다."));
        product.minusLike();
    }

    /**
     * 상품의 리뷰 조회
     * */
    public List<Review> getAllReviewProduct(Long productId){
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("해당 아이디를 가진 상품이 존재하지 않습니다."));
        return product.getReview();
    }

    /**
     * 새로운 이미지 추가, 이미지 삭제
     * */
    @Transactional
    public Product addProductImage(Long productId, List<MultipartFile> multipartFiles) throws IOException{

        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("해당 아이디를 가진 상품이 존재하지 않습니다."));
        List<Image> images = imageService.uploadProductImage(multipartFiles, productId);

        product.uploadProductImage(images);

        return product;
    }

    @Transactional
    public Product deleteProductImage(Long productId, Long imageId){
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("해당 아이디를 가진 상품이 존재하지 않습니다."));
        if(product.getImages() == null){
            throw new RuntimeException("해당 상품에 등록된 이미지가 존재하지 않습니다.");
        }

        product.deleteProductImage(imageService.getOneImage(imageId));

        return product;
    }

    /**
     * functions ---------------------------------------------------------------------------------------------
     * */

    // 상품 리스트 중 sale 상태만 찾기
    public List<Product> findSaleStatusProducts(List<Product> products){
        List<Product> resultProducts = new ArrayList<>();
        for(Product p : products){
            if(p.getStatus().equals(Status.SALE)){
                resultProducts.add(p);
            }
        }
        return resultProducts;
    }
}
