package fftl.usedtradingapi.review.dto;

import fftl.usedtradingapi.review.domain.Review;
import lombok.Builder;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

@Builder
@Getter
public class ReviewResponse {
    private Long id;
    private String content;

    private Long userId;
    private Long productId;

    public static ReviewResponse toResponse(Review review){
        return ReviewResponse.builder()
            .id(review.getId())
            .content(review.getContent())
            .userId(review.getUser().getId())
            .productId(review.getProduct().getId())
            .build();
    }

    public static List<ReviewResponse> toResponse(List<Review> reviews){
        List<ReviewResponse> reviewResponses = new LinkedList<>();

        for(Review review : reviews){
            reviewResponses.add(
                ReviewResponse.builder()
                .id(review.getId())
                .content(review.getContent())
                .userId(review.getUser().getId())
                .productId(review.getProduct().getId())
                .build()
            );
        }
        return reviewResponses;
    }
}
