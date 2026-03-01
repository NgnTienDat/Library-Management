package com.ou.oulib.controller;

import com.ou.oulib.dto.request.BookCreationRequest;
import com.ou.oulib.service.BookService;
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

import java.io.IOException;


@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookController {

    UserService userService;
    BookService bookService;


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<?>> addNewBook(
            @RequestPart("metadata") @Valid BookCreationRequest bookCreationRequest,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail
    ) throws IOException {


        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseUtils.created(bookService.addNewBook(bookCreationRequest, file, thumbnail)));
    }

}
