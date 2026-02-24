package com.ou.oulib.controller;

import com.ou.oulib.dto.request.UserCreationRequest;
import com.ou.oulib.dto.request.UserUpdateRequest;
import com.ou.oulib.dto.response.UserResponse;
import com.ou.oulib.service.UserService;
import com.ou.oulib.utils.ApiResponse;
import com.ou.oulib.utils.ResponseUtils;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

    UserService userService;


    @GetMapping("/health")
    public String healthCheck() {
        return "App is up and running!";
    }


    @PostMapping
    public ResponseEntity<ApiResponse<?>> createUser(@RequestBody @Valid UserCreationRequest userRequest) {


        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseUtils.created(userService.createUser(userRequest)));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(ResponseUtils.ok(userService.getCurrentUser(jwt)));
    }

    @PutMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<UserResponse>> updateMyProfile(
            @AuthenticationPrincipal Jwt jwt,
            @RequestPart("data") @Valid UserUpdateRequest request,
            @RequestPart(value = "avatar", required = false) MultipartFile avatar
    ) {
        return ResponseEntity.ok(ResponseUtils.ok(userService.updateMyProfile(jwt, request, avatar)));
    }








}
