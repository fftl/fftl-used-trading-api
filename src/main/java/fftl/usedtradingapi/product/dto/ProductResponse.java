package fftl.usedtradingapi.product.dto;

import fftl.usedtradingapi.commons.domain.Category;
import fftl.usedtradingapi.image.domain.Image;
import fftl.usedtradingapi.image.dto.ImageResponse;
import fftl.usedtradingapi.product.domain.Address;
import fftl.usedtradingapi.product.domain.Product;
import fftl.usedtradingapi.product.domain.Status;
import fftl.usedtradingapi.review.domain.Review;
import fftl.usedtradingapi.review.dto.ReviewResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor
public class ProductResponse {

    private Long id;
    private String title;
    private Category category;
    private String price;
    private String description;
    private Status status;

    private Address address;
    private Integer like;
    private Long userId;
    private List<ImageResponse> images;
    private List<ReviewResponse> reviews;

    public static List<ImageResponse> imageToImageResponse(List<Image> images){
        List<ImageResponse> imageResponses = new ArrayList<>();
        for(Image image: images){
            imageResponses.add(ImageResponse.builder()
                    .id(image.getId())
                    .productId(image.getProduct().getId())
                    .url(image.getUrl())
                    .build());
        }

        return imageResponses;
    }

    public static List<ReviewResponse> reviewToReviewResponse(List<Review> reviews){
        List<ReviewResponse> reviewResponses = new ArrayList<>();
        for(Review review: reviews){
            reviewResponses.add(ReviewResponse.builder()
                    .id(review.getId())
                    .content(review.getContent())
                    .productId(review.getProduct().getId())
                    .userId(review.getUser().getId())
                    .build());
        }

        return reviewResponses;
    }

    public static ProductResponse toResponse(Product product){

        return ProductResponse.builder()
            .id(product.getId())
            .title(product.getTitle())
            .category(product.getCategory())
            .price(product.getPrice())
            .description(product.getDescription())
            .status(product.getStatus())

            .address(product.getAddress())
            .like(product.getLike())
            .userId(product.getUser().getId())
            .images(imageToImageResponse(product.getImages()))
            .reviews(reviewToReviewResponse(product.getReview()))
            .build();
    }

    public static List<ProductResponse> toResponse(List<Product> products){

        List<ProductResponse> productResponses  = new LinkedList<>();

        for(Product product : products){
            productResponses.add(ProductResponse.builder()
            .id(product.getId())
            .title(product.getTitle())
            .category(product.getCategory())
            .price(product.getPrice())
            .description(product.getDescription())
            .status(product.getStatus())
            .address(product.getAddress())
            .like(product.getLike())
            .userId(product.getUser().getId())
            .images(imageToImageResponse(product.getImages()))
            .reviews(reviewToReviewResponse(product.getReview()))
            .build());
        }

        return productResponses;
    }
}
