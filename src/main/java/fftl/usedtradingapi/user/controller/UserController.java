package fftl.usedtradingapi.user.controller;

import fftl.usedtradingapi.commons.dto.Response;
import fftl.usedtradingapi.user.domain.User;
import fftl.usedtradingapi.user.dto.LoginUserRequest;
import fftl.usedtradingapi.user.dto.SaveUserRequest;
import fftl.usedtradingapi.user.dto.UserResponse;
import fftl.usedtradingapi.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequestMapping("/user")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public Response saveUser(@RequestBody SaveUserRequest saveUserRequest) throws IOException{
        UserResponse userResponse = UserResponse.toResponse(userService.saveUser(saveUserRequest));
        return new Response(true, null, userResponse);
    }

    @PostMapping("/login")
    public Response loginUser(@RequestBody LoginUserRequest loginUserRequest){
        UserResponse userResponse = UserResponse.toResponse(userService.loginUser(loginUserRequest));
        return new Response(true, null, userResponse);
    }

    @GetMapping("/{userId}")
    public Response getOneUser(@PathVariable Long userId){
        UserResponse userResponse = UserResponse.toResponse(userService.getOneUser(userId));
        return new Response(true, null, userResponse);
    }

    @PatchMapping("/{userId}")
    public Response updateUser(@PathVariable Long userId, @RequestBody SaveUserRequest saveUserRequest){
        UserResponse userResponse = UserResponse.toResponse(userService.updateUser(userId, saveUserRequest));
        return new Response(true, null, userResponse);
    }

    @DeleteMapping("/{userId}")
    public Response deleteUser(@PathVariable Long userId){
        userService.deleteUser(userId);
        return new Response(true, null);
    }

    @PostMapping("/category/{userId}")
    public Response addUserCategory(@PathVariable Long userId, @RequestParam("categoryId") Long categoryId){
        UserResponse userResponse = UserResponse.toResponse(userService.addUserCategory(userId, categoryId));
        return new Response(true, null, userResponse);
    }

    @DeleteMapping("/category/{userId}")
    public Response deleteCategory(@PathVariable Long userId, @RequestParam("categoryId") Long categoryId){
        UserResponse userResponse = UserResponse.toResponse(userService.deleteUserCateogry(userId, categoryId));
        return new Response(true, null, userResponse);
    }

}
