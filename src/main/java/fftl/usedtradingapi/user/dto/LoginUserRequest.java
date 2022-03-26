package fftl.usedtradingapi.user.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginUserRequest {

    private String username;
    private String password;
}
