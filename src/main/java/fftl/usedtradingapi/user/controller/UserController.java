package fftl.usedtradingapi.user.controller;

import fftl.usedtradingapi.commons.dto.Response;
import fftl.usedtradingapi.user.domain.User;
import fftl.usedtradingapi.user.dto.LoginUserRequest;
import fftl.usedtradingapi.user.dto.SaveUserRequest;
import fftl.usedtradingapi.user.dto.UserResponse;
import fftl.usedtradingapi.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.Collections;

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
        UserResponse userResponse = UserResponse.toResponse(userService.deleteUserCategory(userId, categoryId));
        return new Response(true, null, userResponse);
    }

    @PostMapping("/wishProduct/{userId}/{productId}")
    public Response addWishProduct(@PathVariable Long userId, @PathVariable Long productId){
        UserResponse userResponse = UserResponse.toResponse(userService.addWishProduct(userId, productId));
        return new Response(true, null, userResponse);
    }

    @DeleteMapping("/wishProduct/{userId}/{productId}")
    public Response deleteWishProduct(@PathVariable Long userId, @PathVariable Long productId){
        UserResponse userResponse = UserResponse.toResponse(userService.deleteWishProduct(userId, productId));
        return new Response(true, null, userResponse);
    }

    @PostMapping("/image")
    public Response addUserImage(@PathVariable Long userId, @RequestParam("multipartFile") MultipartFile multipartFile) throws IOException{
        UserResponse userResponse = UserResponse.toResponse(userService.addUserImage(userId, multipartFile));
        return new Response(true, null, userResponse);
    }

    @DeleteMapping("/image")
    public Response deleteUserImage(@PathVariable Long userId) throws IOException{
        UserResponse userResponse = UserResponse.toResponse(userService.deleteUserImage(userId));
        return new Response(true, null, userResponse);
    }

}
