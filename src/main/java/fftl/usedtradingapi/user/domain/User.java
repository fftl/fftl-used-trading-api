package fftl.usedtradingapi.user.domain;

import fftl.usedtradingapi.commons.domain.Category;
import fftl.usedtradingapi.image.domain.Image;
import fftl.usedtradingapi.product.domain.Address;
import fftl.usedtradingapi.product.domain.Product;
import fftl.usedtradingapi.review.domain.Review;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "users_id")
    private Long id;

    @Column(name = "users_username")
    private String username;

    @Column(name = "users_password")
    private String password;

    //관심 카테고리
    @OneToMany(mappedBy = "user")
    private List<Category> categories = new ArrayList<>();

    //내 동네 설정
    @Embedded
    private Address addresses;

    //상품 목록
    @OneToMany(mappedBy = "user")
    private List<Product> products = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Review> reviews = new ArrayList<>();

    private Image image;

    @Column(name = "users_removed")
    private boolean removed;
}
