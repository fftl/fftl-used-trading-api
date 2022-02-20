package fftl.usedtradingapi.image.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ImageResponse {

    private Long id;
    private String url;
    private Long userId;
    private Long productId;

    public static ImageResponse toResponse(){

    }
}
