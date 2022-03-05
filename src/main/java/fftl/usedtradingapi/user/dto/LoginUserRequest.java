package fftl.usedtradingapi.user.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LoginUserRequest {

    private String username;
    private String password;
}
