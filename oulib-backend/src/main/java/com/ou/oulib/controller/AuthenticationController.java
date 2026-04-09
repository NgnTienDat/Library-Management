package com.ou.oulib.controller;


import com.nimbusds.jose.JOSEException;
import com.ou.oulib.dto.request.AuthenticationRequest;
import com.ou.oulib.dto.request.IntrospectRequest;
import com.ou.oulib.dto.request.LogoutRequest;
import com.ou.oulib.dto.request.RefreshRequest;
import com.ou.oulib.dto.response.AuthenticationResponse;
import com.ou.oulib.dto.response.IntrospectResponse;
import com.ou.oulib.service.AuthenticationService;
import com.ou.oulib.utils.ApiResponse;
import com.ou.oulib.utils.ResponseUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@Slf4j
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Nhóm API xác thực người dùng: đăng nhập, đăng xuất, kiểm tra token và làm mới token")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {

    AuthenticationService authenticationService;

    @PostMapping("/login")
        @Operation(
            summary = "Đăng nhập người dùng",
            description = "Xác thực email và mật khẩu để trả về thông tin tài khoản cùng JWT truy cập"
        )
        @ApiResponses(value = {
                @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Đăng nhập thành công"),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dữ liệu đăng nhập không hợp lệ"),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Sai thông tin xác thực"),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy người dùng"),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
        })
    public ResponseEntity<ApiResponse<?>> authenticate(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Thông tin đăng nhập gồm email và mật khẩu",
                required = true
            )
            @RequestBody @Valid AuthenticationRequest authenticationRequest) {

        AuthenticationResponse result = authenticationService.authenticate(authenticationRequest);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseUtils.ok(result));
    }

    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
        @Operation(
            summary = "Đăng xuất phiên hiện tại",
            description = "Thu hồi token hiện tại bằng cách đưa token vào danh sách đã vô hiệu hóa"
        )
        @ApiResponses(value = {
                @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Đăng xuất thành công"),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Chưa xác thực hoặc token không hợp lệ"),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Không có quyền truy cập"),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
        })
            public ApiResponse<Void> logout(
                @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Token cần đưa vào danh sách vô hiệu hóa khi đăng xuất",
                    required = true
                )
                @RequestBody LogoutRequest logoutRequest) throws ParseException, JOSEException {
        this.authenticationService.logout(logoutRequest);
        return ResponseUtils.buildResponse(null, "Logout", HttpStatus.OK);
    }

    @PostMapping("/introspect")
        @Operation(
            summary = "Kiểm tra tính hợp lệ của token",
            description = "Xác minh token còn hiệu lực hay đã hết hạn/không hợp lệ"
        )
        @ApiResponses(value = {
                @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Kiểm tra token thành công"),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dữ liệu token không hợp lệ"),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
        })
    public ResponseEntity<ApiResponse<IntrospectResponse>> introspect(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Token cần kiểm tra trạng thái hợp lệ",
                required = true
            )
            @RequestBody @Valid IntrospectRequest introspectRequest) throws ParseException, JOSEException {

        IntrospectResponse result = authenticationService.introspect(introspectRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseUtils.buildResponse(result, "Introspection Successful", HttpStatus.OK));

    }

    @PostMapping("/refresh")
        @Operation(
            summary = "Làm mới access token",
            description = "Cấp token mới dựa trên token hiện tại còn trong cửa sổ làm mới"
        )
        @ApiResponses(value = {
                @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Làm mới token thành công"),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token không hợp lệ hoặc đã hết hạn"),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
        })
    public ResponseEntity<ApiResponse<AuthenticationResponse>> refreshToken(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Token hiện tại dùng để làm mới phiên",
                required = true
            )
            @RequestBody RefreshRequest refreshRequest) throws ParseException, JOSEException {

        AuthenticationResponse result = authenticationService.refreshToken(refreshRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseUtils.buildResponse(result, "Refresh token", HttpStatus.OK));
    }

}
