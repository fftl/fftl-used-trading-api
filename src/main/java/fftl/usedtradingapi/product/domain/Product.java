package fftl.usedtradingapi.product.domain;

import fftl.usedtradingapi.commons.domain.Category;
import fftl.usedtradingapi.image.domain.Image;
import fftl.usedtradingapi.review.domain.Review;
import fftl.usedtradingapi.user.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Builder
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
    @OneToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "product_price")
    private String price;

    @Column(name = "product_description")
    private String description;

    //상태
    @Enumerated(EnumType.STRING)
    @Column(name = "product_status")
    private Status status;

    //주소
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "state", column = @Column(name = "product_state")),
        @AttributeOverride(name = "city", column = @Column(name = "product_city")),
        @AttributeOverride(name = "town", column = @Column(name = "product_town")),
    })
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

    //상품 이미지
    @OneToMany(mappedBy = "product")
    private List<Image> images = new ArrayList<>();


    public void plusLike(){
        this.like++;
    }

    public void minusLike(){
        this.like--;
    }

    public void statusSale() {
        this.status = Status.SALE;
    }

    public void statusComplete() {
        this.status = Status.COMPLETE;
    }

    public void statusCancel() {
        this.status = Status.CANCEL;
    }
}
