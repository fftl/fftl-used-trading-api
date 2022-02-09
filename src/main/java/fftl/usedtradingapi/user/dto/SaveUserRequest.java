package fftl.usedtradingapi.user.dto;

import fftl.usedtradingapi.commons.domain.Category;
import fftl.usedtradingapi.product.domain.Address;
import fftl.usedtradingapi.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SaveUserRequest {

    private String username;
    private String password;
    private List<Category> categories;
    private Address address;

    public User toEntity(){
        return User.builder()
            .address(address)
            .categories(categories)
            .username(username)
            .password(password)
            .removed(false)
            .build();
    }

}
