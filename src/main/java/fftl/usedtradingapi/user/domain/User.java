package fftl.usedtradingapi.user.domain;

import fftl.usedtradingapi.commons.domain.Category;
import fftl.usedtradingapi.image.domain.Image;
import fftl.usedtradingapi.product.domain.Address;
import fftl.usedtradingapi.product.domain.Product;
import fftl.usedtradingapi.review.domain.Review;
import fftl.usedtradingapi.user.dto.SaveUserRequest;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
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
    @OneToMany
    @Builder.Default
    private List<Category> categories = new ArrayList<>();

    //내 동네 설정
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "state", column = @Column(name = "users_state")),
        @AttributeOverride(name = "city", column = @Column(name = "users_city")),
        @AttributeOverride(name = "town", column = @Column(name = "users_town")),
    })
    private Address address;

    //상품 목록
    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<Product> myProducts = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<Product> wishProducts = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<Review> reviews = new ArrayList<>();

    @OneToOne(mappedBy = "user")
    private Image image;

    @Column(name = "users_removed")
    private boolean removed;

    /**
     * functions ---------------------------------------------------------------------------------------------
     * */

    public void updateUser(SaveUserRequest request){
        this.password = request.getPassword();
        this.categories = request.getCategories();
        this.address = Address.builder().state(request.getState()).city(request.getCity()).town(request.getTown()).build();
    }

    public void addWishProduct(Product product){
        this.wishProducts.add(product);
    }

    public void deleteWishProduct(Product product){
        this.wishProducts.remove(product);
    }

    public void addCategory(Category category){
        this.categories.add(category);
    }

    public void deleteCategory(Category category){
        this.categories.remove(category);
    }

    public void addUserImage(Image image){
        this.image = image;
    }

    public void deleteUserImage(){
        this.image = null;
    }

    public void deleteUser(){
        this.removed = true;
    }
}
