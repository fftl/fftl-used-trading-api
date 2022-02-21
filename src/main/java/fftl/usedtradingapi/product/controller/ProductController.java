package fftl.usedtradingapi.product.controller;

import fftl.usedtradingapi.commons.dto.Response;
import fftl.usedtradingapi.product.domain.Product;
import fftl.usedtradingapi.product.dto.ProductResponse;
import fftl.usedtradingapi.product.dto.SaveProductRequest;
import fftl.usedtradingapi.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/product")
@RestController
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public Response saveProduct(@RequestBody SaveProductRequest saveProductRequest) throws IOException {
        ProductResponse productResponse = ProductResponse.toResponse(productService.saveProduct(saveProductRequest));
        return new Response(true, null, productResponse);
    }

    @GetMapping
    public Response getAllProduct(){
        List<ProductResponse> productResponses = ProductResponse.toResponse(productService.getAllProduct());
        return new Response(true, null, productResponses);
    }

    @GetMapping("/{productId}")
    public Response getOneProduct(@PathVariable Long productId){
        ProductResponse productResponse = ProductResponse.toResponse(productService.getOneProduct(productId));
        return new Response(true, null, productResponse);
    }

    /**
     * 상품 정보 수정
     * */
    @PatchMapping("/update/{productId}")
    public Response updateProduct(@PathVariable Long productId, @RequestBody SaveProductRequest saveProductRequest) throws IOException{
        ProductResponse productResponse = ProductResponse.toResponse(productService.updateProduct(productId, saveProductRequest));
        return new Response(true, null, productResponse);
    }


    /**
     * 상품 상태 변경(sale(판매중), complete(판매완료), cancel(판매취소))
     * */
    @PatchMapping("/cancel/{productId}")
    public Response cancelProduct(@PathVariable Long productId){
        productService.cancelProduct(productId);
        return new Response(true, null);
    }

    @PatchMapping("/complete/{productId}")
    public Response completeProduct(@PathVariable Long productId){
        productService.completeProduct(productId);
        return new Response(true, null);
    }

    @PatchMapping("/sale/{productId}")
    public Response saleProduct(@PathVariable Long productId){
        productService.saleProduct(productId);
        return new Response(true, null);
    }


}
