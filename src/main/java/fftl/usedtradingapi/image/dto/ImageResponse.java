package fftl.usedtradingapi.image.dto;

import fftl.usedtradingapi.image.domain.Image;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ImageResponse {

    private Long id;
    private String url;
    private Long userId;
    private Long productId;

    public static ImageResponse toResponse(Image image){
        return ImageResponse.builder()
            .id(image.getId())
            .url(image.getUrl())
            .userId(image.getUser().getId())
            .productId(image.getProduct().getId())
            .build();
    }
}
