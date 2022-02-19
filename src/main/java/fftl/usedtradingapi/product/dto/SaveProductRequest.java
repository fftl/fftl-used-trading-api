package fftl.usedtradingapi.product.dto;

import fftl.usedtradingapi.commons.domain.Category;
import fftl.usedtradingapi.image.domain.Image;
import fftl.usedtradingapi.product.domain.Address;
import fftl.usedtradingapi.product.domain.Product;
import fftl.usedtradingapi.product.domain.Status;
import fftl.usedtradingapi.user.domain.User;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
public class SaveProductRequest {

    private String title;
    private Category category;
    private String price;
    private String description;
    private Status status;

    //Address
    private String state;
    private String city;
    private String town;

    private List<MultipartFile> files;

    private List<Image> images;
    private User user;

    public Product toEntity(){
        return Product.builder()
            .title(title)
            .category(category)
            .price(price)
            .description(description)
            .status(status)
            .address(Address.builder().state(state).city(city).town(town).build())
            .build();
    }
}
