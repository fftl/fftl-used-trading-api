package fftl.usedtradingapi.review.domain;

import fftl.usedtradingapi.product.domain.Product;
import fftl.usedtradingapi.user.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
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
}
