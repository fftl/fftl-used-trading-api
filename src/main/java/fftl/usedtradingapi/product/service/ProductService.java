package fftl.usedtradingapi.product.service;

import fftl.usedtradingapi.commons.utils.S3Uploader;
import fftl.usedtradingapi.image.domain.Image;
import fftl.usedtradingapi.image.service.ImageService;
import fftl.usedtradingapi.product.domain.Product;
import fftl.usedtradingapi.product.domain.ProductRepository;
import fftl.usedtradingapi.product.domain.Status;
import fftl.usedtradingapi.product.dto.SaveProductRequest;
import fftl.usedtradingapi.review.domain.Review;
import fftl.usedtradingapi.user.domain.User;
import fftl.usedtradingapi.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final UserService userService;
    private final ImageService imageService;

    /**
     * imageUpload 완료하고 작성
     * */
    public Product saveProduct(SaveProductRequest saveProductRequest, Long userId) throws IOException {

        // User Entity 가져오기
        User user = userService.getOneUser(userId);
        saveProductRequest.setUser(user);

        // Product 등록하고 ProductId를 가져옴
        // 그리고 ProductId 에 image를 등록합니다.
        Product product = productRepository.save(saveProductRequest.toEntity());
        List<Image> images = imageService.uploadProductImage(saveProductRequest.getFiles(), product.getId());

        // 등록한 image들을 가지고 product에 다시 등록
        product.productImageUpload(images);

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
    // 판매완료 처리
    public void  saleProduct(Long productId){
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("해당 아이디를 가진 상품이 존재하지 않습니다."));
        product.statusSale();
    }

    // 판매완료 처리
    public void  completeProduct(Long productId){
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("해당 아이디를 가진 상품이 존재하지 않습니다."));
        product.statusComplete();
    }

    // 주문취소 처리
    public void  cancelProduct(Long productId){
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("해당 아이디를 가진 상품이 존재하지 않습니다."));
        product.statusCancel();
    }

    /**
     * 좋아요 증가 감소
     * */

    // 좋아요 증가
    public void plusLike(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("해당 아이디를 가진 상품이 존재하지 않습니다."));
        product.plusLike();
    }

    // 좋아요 감소
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
