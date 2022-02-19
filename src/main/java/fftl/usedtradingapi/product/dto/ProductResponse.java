package fftl.usedtradingapi.product.dto;

import fftl.usedtradingapi.commons.domain.Category;
import fftl.usedtradingapi.image.domain.Image;
import fftl.usedtradingapi.product.domain.Address;
import fftl.usedtradingapi.product.domain.Status;
import fftl.usedtradingapi.review.domain.Review;
import lombok.Getter;

import java.util.List;

@Getter
public class ProductResponse {

    private Long id;
    private String title;
    private Category category;
    private String price;
    private String description;
    private Status status;

    private Address address;
    private Integer like;
    private Long userId;
    private List<Image> images;
    private List<Review> reviews;

}
