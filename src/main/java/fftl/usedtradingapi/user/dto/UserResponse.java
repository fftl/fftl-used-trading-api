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
    private String username;
    private List<Category> categories;
    private Address address;
    private List<Product> myProducts;
    private List<Product> wishProducts;
    private List<Review> reviews;
    private Image image;

    public static UserResponse toResponse(User user){
        return UserResponse.builder()
            .id(user.getId())
            .username(user.getUsername())
            .categories(user.getCategories())
            .address(user.getAddress())
            .myProducts(user.getMyProducts())
            .wishProducts(user.getWishProducts())
            .reviews(user.getReviews())
            .image(user.getImage())
            .build();
    }
}
