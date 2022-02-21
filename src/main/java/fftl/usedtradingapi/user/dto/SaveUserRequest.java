package fftl.usedtradingapi.user.dto;

import fftl.usedtradingapi.commons.domain.Category;
import fftl.usedtradingapi.image.domain.Image;
import fftl.usedtradingapi.product.domain.Address;
import fftl.usedtradingapi.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SaveUserRequest {

    private String username;
    private String password;
    private List<Long> categoryIds;
    private String state;
    private String city;
    private String town;
    private MultipartFile multipartFile;

    private Image image;
    private List<Category> categories;

    public User toEntity(){
        return User.builder()
            .username(username)
            .password(password)
            .address(Address.builder().state(state).city(city).town(town).build())
            .image(image)
            .categories(categories)
            .removed(false)
            .build();
    }

}
