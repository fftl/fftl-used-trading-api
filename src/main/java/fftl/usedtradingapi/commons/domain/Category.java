package fftl.usedtradingapi.commons.domain;

import fftl.usedtradingapi.product.domain.Product;
import fftl.usedtradingapi.user.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Getter
@NoArgsConstructor
@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @Column(name = "category_name")
    private String categoryName;

}
