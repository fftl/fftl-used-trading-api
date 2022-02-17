package fftl.usedtradingapi.product.controller;

import fftl.usedtradingapi.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/product")
@RestController
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public boolean saveProduct(){
        return true;
    }

    @GetMapping
    public boolean getAllProduct(){
        return true;
    }

    @GetMapping("/{productId}")
    public boolean getOneProduct(){
        return true;
    }

    @DeleteMapping("/{productId}")
    public boolean deleteProduct(){
        return true;
    }
}
