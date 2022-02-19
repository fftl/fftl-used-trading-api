package fftl.usedtradingapi.product.service;

import fftl.usedtradingapi.commons.utils.S3Uploader;
import fftl.usedtradingapi.product.domain.Product;
import fftl.usedtradingapi.product.domain.ProductRepository;
import fftl.usedtradingapi.product.domain.Status;
import fftl.usedtradingapi.product.dto.SaveProductRequest;
import fftl.usedtradingapi.review.domain.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final S3Uploader s3Uploader;

    /**
     * imageUpload 완료하고 작성
     * */
    public boolean saveProduct(SaveProductRequest saveProductRequest){
        return true;
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
        List<Product> products = findSaleStatusProducts(productRepository.findByState(state));
        return products;
    }

    public List<Product> getProductByCity(String city) {
        List<Product> products = findSaleStatusProducts(productRepository.findByCity(city));
        return products;
    }

    public List<Product> getProductByTown(String town) {
        List<Product> products = findSaleStatusProducts(productRepository.findByTown(town));
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
