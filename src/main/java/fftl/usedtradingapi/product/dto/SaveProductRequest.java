package fftl.usedtradingapi.product.dto;

import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
public class SaveProductRequest {

    private String title;
    private String category;
    private String price;
    private String description;
    private String status;

    private String state;
    private String city;
    private String town;

    private List<MultipartFile> files;
}
