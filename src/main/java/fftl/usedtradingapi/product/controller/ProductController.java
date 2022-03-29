package fftl.usedtradingapi.product.controller;

import fftl.usedtradingapi.commons.dto.Response;
import fftl.usedtradingapi.product.dto.ProductResponse;
import fftl.usedtradingapi.product.dto.SaveProductRequest;
import fftl.usedtradingapi.product.service.ProductService;
import fftl.usedtradingapi.review.dto.ReviewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    /**
     * 상품 조회
     * */
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
     * 지역별 상품 조회
     * */
    //TODO 소분류 지역은 대분류도 받아서 좁혀줘야 할지 고민
    @GetMapping("/state")
    public Response getProductByState(@RequestParam("state") String state){
        List<ProductResponse> productResponses = ProductResponse.toResponse(productService.getProductByState(state));
        return new Response(true, null, productResponses);
    }

    @GetMapping("/city")
    public Response getProductByCity(@RequestParam("city") String city){
        List<ProductResponse> productResponses = ProductResponse.toResponse(productService.getProductByCity(city));
        return new Response(true, null, productResponses);
    }

    @GetMapping("/town")
    public Response getProductByTown(@RequestParam("town") String town){
        List<ProductResponse> productResponses = ProductResponse.toResponse(productService.getProductByTown(town));
        return new Response(true, null, productResponses);
    }

    /**
     * 좋아요 증가 감소
     * */
    @PatchMapping("/plusLike/{productId}")
    public Response plusLike(@PathVariable Long productId){
        productService.plusLike(productId);
        return new Response(true, null);
    }

    @PatchMapping("/minusLike/{productId}")
    public Response minusLike(@PathVariable Long productId){
        productService.minusLike(productId);
        return new Response(true, null);
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

    /**
     * 상품 이미지 추가, 삭제
     * */

    @PostMapping("/image/{productId}")
    public Response addProductImage(@PathVariable Long productId, @RequestParam("multipartFiles") List<MultipartFile> multipartFiles) throws IOException {
        ProductResponse productResponse = ProductResponse.toResponse(productService.addProductImage(productId, multipartFiles));
        return new Response(true, null, productResponse);
    }

    @DeleteMapping("/image/{productId}/{imageId}")
    public Response deleteProductImage(@PathVariable Long productId, @PathVariable Long imageId) throws IOException {
        ProductResponse productResponse = ProductResponse.toResponse(productService.deleteProductImage(productId, imageId));
        return new Response(true, null, productResponse);
    }

    /**
     * 해당 상품의 리뷰 전부 가져오기
     * */
    @GetMapping("/review/{productId}")
    public Response getAllReviewProduct(@PathVariable Long productId){
        List<ReviewResponse> reviewResponses = ReviewResponse.toResponse(productService.getAllReviewProduct(productId));
        return new Response(true, null, reviewResponses);
    }
}
