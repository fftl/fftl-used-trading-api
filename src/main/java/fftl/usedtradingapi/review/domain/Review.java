package fftl.usedtradingapi.review.domain;

import fftl.usedtradingapi.product.domain.Product;
import fftl.usedtradingapi.review.dto.SaveReviewRequest;
import fftl.usedtradingapi.user.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@Table(name = "review")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @Column(name = "review_content")
    private String content;

    //회원
    @ManyToOne
    @JoinColumn(name = "users_id")
    private User user;

    //상품
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    public void updateReview(String content){
        this.content = content;
    }

}
