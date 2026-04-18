package com.ou.oulib.controller;

import com.ou.oulib.dto.request.UserCreationRequest;
import com.ou.oulib.dto.request.ChangePasswordRequest;
import com.ou.oulib.dto.request.StaffCreationRequest;
import com.ou.oulib.dto.request.UserStatusUpdateRequest;
import com.ou.oulib.dto.request.UserUpdateRequest;
import com.ou.oulib.dto.response.UserResponse;
import com.ou.oulib.service.UserService;
import com.ou.oulib.utils.ApiResponse;
import com.ou.oulib.utils.PageResponse;
import com.ou.oulib.utils.ResponseUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "Nhóm API quản lý tài khoản người dùng, hồ sơ cá nhân và tài khoản nhân sự")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

    UserService userService;

    @PostMapping
    @Operation(summary = "Đăng ký tài khoản người dùng", description = "Tạo mới tài khoản người dùng với vai trò USER từ thông tin đăng ký cơ bản")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Tạo người dùng thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dữ liệu đăng ký không hợp lệ"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Người dùng đã tồn tại"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
    })
    public ResponseEntity<ApiResponse<?>> createUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Thông tin đăng ký tài khoản người dùng", required = true) @RequestBody @Valid UserCreationRequest userRequest) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseUtils.created(userService.createUser(userRequest)));
    }

    @PostMapping("/staff")
    @PreAuthorize("hasRole('SYSADMIN')")
    @Operation(summary = "Tạo tài khoản nhân sự", description = "Cho phép SYSADMIN tạo tài khoản LIBRARIAN hoặc SYSADMIN mới")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Tạo tài khoản nhân sự thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Chưa xác thực"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Không có quyền thực hiện"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Email đã được sử dụng"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
    })
    public ResponseEntity<ApiResponse<?>> createStaff(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Thông tin tài khoản nhân sự cần tạo", required = true) @RequestBody @Valid StaffCreationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseUtils.created(userService.createStaff(request)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SYSADMIN','LIBRARIAN')")
    @Operation(summary = "Lấy danh sách người dùng", description = "Trả về danh sách người dùng có phân trang cho SYSADMIN hoặc LIBRARIAN")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy danh sách người dùng thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Chưa xác thực"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Không có quyền thực hiện"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
    })
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getUsers(
            @Parameter(description = "Số trang, bắt đầu từ 0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Số phần tử mỗi trang") @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ResponseUtils.ok(userService.getUsers(page, size)));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('SYSADMIN','LIBRARIAN')")
    @Operation(summary = "Cập nhật trạng thái người dùng", description = "Cập nhật trạng thái tài khoản theo userId cho SYSADMIN hoặc LIBRARIAN")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cập nhật trạng thái thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dữ liệu trạng thái không hợp lệ"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Chưa xác thực"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Không có quyền thực hiện"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy người dùng"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
    })
    public ResponseEntity<ApiResponse<UserResponse>> updateUserStatus(
            @Parameter(description = "ID của người dùng cần cập nhật trạng thái") @PathVariable String id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Trạng thái mới của tài khoản người dùng", required = true) @RequestBody @Valid UserStatusUpdateRequest request) {
        return ResponseEntity.ok(ResponseUtils.ok(userService.updateUserStatus(id, request)));
    }

    @GetMapping("/me")
    @Operation(summary = "Lấy hồ sơ người dùng hiện tại", description = "Lấy thông tin hồ sơ của tài khoản đang đăng nhập dựa trên JWT")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy hồ sơ thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Chưa xác thực"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy người dùng"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
    })
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(ResponseUtils.ok(userService.getCurrentUser(jwt)));
    }

    @PutMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Cập nhật hồ sơ cá nhân", description = "Cập nhật thông tin hồ sơ của tài khoản hiện tại bằng dữ liệu multipart")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cập nhật hồ sơ thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dữ liệu cập nhật không hợp lệ"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Chưa xác thực"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy người dùng"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dữ liệu multipart gồm phần data chứa thông tin hồ sơ và phần avatar tùy chọn", required = true)
    public ResponseEntity<ApiResponse<UserResponse>> updateMyProfile(
            @AuthenticationPrincipal Jwt jwt,
            @Parameter(description = "Dữ liệu hồ sơ người dùng cần cập nhật") @RequestPart("data") @Valid UserUpdateRequest request,
            @Parameter(description = "Tệp ảnh đại diện tải lên, có thể bỏ trống") @RequestPart(value = "avatar", required = false) MultipartFile avatar) {
        return ResponseEntity.ok(ResponseUtils.ok(userService.updateMyProfile(jwt, request, avatar)));
    }

    @PutMapping("/me/password")
    @Operation(summary = "Đổi mật khẩu tài khoản hiện tại", description = "Người dùng đã đăng nhập đổi mật khẩu bằng cách cung cấp mật khẩu cũ và mật khẩu mới")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Đổi mật khẩu thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dữ liệu mật khẩu không hợp lệ"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Chưa xác thực hoặc mật khẩu cũ không đúng"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy người dùng"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
    })
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal Jwt jwt,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Thông tin mật khẩu cũ và mật khẩu mới", required = true) @RequestBody @Valid ChangePasswordRequest request) {
        userService.changePassword(jwt, request);
        return ResponseEntity.ok(ResponseUtils.buildResponse(null, "Password changed successfully", HttpStatus.OK));
    }

}
