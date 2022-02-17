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

    public boolean getOneProduct(){
        return true;
    }

    public boolean deleteProduct(){
        return true;
    }

}
