package fftl.usedtradingapi.product.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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

    @Column(name = "product_price")
    private Integer price;

    @Column(name = "product_price")
    private String description;

    //상태

    //관심도
}
