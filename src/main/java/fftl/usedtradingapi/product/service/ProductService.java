package fftl.usedtradingapi.product.service;

import fftl.usedtradingapi.commons.utils.S3Uploader;
import fftl.usedtradingapi.product.domain.Product;
import fftl.usedtradingapi.product.domain.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final S3Uploader s3Uploader;

    public boolean saveProduct(){
        return true;
    }

    public boolean getAllProduct(){
        return true;
    }

    public boolean getProductByState() { return true; }

    public boolean getProductByCity() { return true; }

    public boolean getProductByTown() { return true; }

    public Product getOneProduct(Long productId){
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("해당 아이디를 가진 상품이 존재하지 않습니다."));
        return product;
    }

    public boolean deleteProduct(){
        return true;
    }

    public boolean plusLike() { return true;
    }

    public boolean minusLike() { return true;
    }

}
