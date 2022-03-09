package fftl.usedtradingapi.review.service;

import fftl.usedtradingapi.product.domain.Product;
import fftl.usedtradingapi.product.service.ProductService;
import fftl.usedtradingapi.review.domain.Review;
import fftl.usedtradingapi.review.domain.ReviewRepository;
import fftl.usedtradingapi.review.dto.SaveReviewRequest;
import fftl.usedtradingapi.user.domain.User;
import fftl.usedtradingapi.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserService userService;
    private final ProductService productService;

    /**
     * 리뷰 저장
     * */
    public Review saveReview(SaveReviewRequest saveReviewRequest){
        User user = userService.getOneUser(saveReviewRequest.getUserId());
        Product product = productService.getOneProduct(saveReviewRequest.getProductId());

        saveReviewRequest.setUser(user);
        saveReviewRequest.setProduct(product);

        Review review = reviewRepository.save(saveReviewRequest.toEntity());

        return review;
    }

    /**
     * 리뷰 수정
     * */
    public Review updateReview(Long reviewId, String content){
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new RuntimeException("해당 아이디를 가진 리뷰가 존재하지 않습니다."));
        review.updateReview(content);

        return review;
    }

    /**
     * 리뷰 삭제
     * */
    public void deleteReview(Long reviewId){
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new RuntimeException("해당 아이디를 가진 리뷰가 존재하지 않습니다."));
        reviewRepository.delete(review);
    }
}
