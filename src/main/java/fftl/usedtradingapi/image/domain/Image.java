package fftl.usedtradingapi.image.domain;

import fftl.usedtradingapi.product.domain.Product;
import fftl.usedtradingapi.user.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "Image")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long id;

    @Column(name = "image_url")
    private String url;

    @Column(name = "image_type")
    private ImageType imageType;

    @Column(name = "image_removed")
    private boolean removed;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

}
