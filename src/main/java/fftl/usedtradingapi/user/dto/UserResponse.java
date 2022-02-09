package fftl.usedtradingapi.user.dto;

import fftl.usedtradingapi.commons.domain.Category;
import fftl.usedtradingapi.image.domain.Image;
import fftl.usedtradingapi.product.domain.Address;
import fftl.usedtradingapi.product.domain.Product;
import fftl.usedtradingapi.review.domain.Review;
import fftl.usedtradingapi.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String ussername;
    private List<Category> categories;
    private Address address;
    private List<Product> products;
    private List<Review> reviews;
    private Image image;

    public static UserResponse toResponse(User user){
        return UserResponse.builder()
            .id(user.getId())
            .ussername(user.getUsername())
            .categories(user.getCategories())
            .address(user.getAddress())
            .products(user.getProducts())
            .reviews(user.getReviews())
            .image(user.getImage())
            .build();
    }
}
