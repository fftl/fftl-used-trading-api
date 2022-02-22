package fftl.usedtradingapi.review.controller;

import fftl.usedtradingapi.commons.dto.Response;
import fftl.usedtradingapi.review.domain.Review;
import fftl.usedtradingapi.review.dto.ReviewResponse;
import fftl.usedtradingapi.review.dto.SaveReviewRequest;
import fftl.usedtradingapi.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/review")
@RequiredArgsConstructor
@RestController
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public Response saveReview(@RequestBody SaveReviewRequest saveReviewRequest){
        ReviewResponse reviewResponse = ReviewResponse.toResponse(reviewService.saveReview(saveReviewRequest));
        return new Response(true, null, reviewResponse);
    }

    @PatchMapping("/{reviewId}")
    public Response updateReview(@PathVariable Long reviewId, @RequestParam("content") String content){
        ReviewResponse reviewResponse = ReviewResponse.toResponse(reviewService.updateReview(reviewId, content));
        return new Response(true, null, reviewResponse);
    }

    @DeleteMapping("/{reviewId}")
    public Response deleteReview(@PathVariable Long reviewId){
        reviewService.deleteReview(reviewId);
        return new Response(true, null);
    }
}
