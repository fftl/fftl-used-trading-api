package fftl.usedtradingapi.user.controller;

import fftl.usedtradingapi.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(name = "/user")
public class UserController {

    private final UserService userService;

}
