package fftl.usedtradingapi.review.dto;

import fftl.usedtradingapi.product.domain.Product;
import fftl.usedtradingapi.review.domain.Review;
import fftl.usedtradingapi.user.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class SaveReviewRequest {

    private String content;
    private Long userId;
    private Long productId;

    private User user;
    private Product product;

    public Review toEntity(){
        return Review.builder()
            .content(content)
            .user(user)
            .product(product)
            .build();
    }
}
