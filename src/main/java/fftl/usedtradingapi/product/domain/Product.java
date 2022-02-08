package fftl.usedtradingapi.product.domain;

import fftl.usedtradingapi.commons.domain.Category;
import fftl.usedtradingapi.image.domain.Image;
import fftl.usedtradingapi.review.domain.Review;
import fftl.usedtradingapi.user.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(name = "product_title")
    private String title;

    //카테고리
    @Column(name = "product_category")
    private Category category;

    @Column(name = "product_price")
    private Integer price;

    @Column(name = "product_price")
    private String description;

    //상태
    @Column(name = "product_state")
    private State state;

    //주소
    @Embedded
    private Address address;

    //관심도
    @Column(name = "product_like")
    private Integer like;

    //유저
    @ManyToOne
    @JoinColumn(name = "users_id")
    private User user;

    //후기
    @OneToMany(mappedBy = "product")
    private List<Review> review = new ArrayList<>();

    //후기
    @OneToMany(mappedBy = "product")
    private List<Image> images = new ArrayList<>();

}
