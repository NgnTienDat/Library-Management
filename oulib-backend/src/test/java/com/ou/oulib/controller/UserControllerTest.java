package com.ou.oulib.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ou.oulib.dto.request.UserCreationRequest;
import com.ou.oulib.dto.request.UserStatusUpdateRequest;
import com.ou.oulib.dto.response.UserResponse;
import com.ou.oulib.enums.ErrorCode;
import com.ou.oulib.enums.UserStatus;
import com.ou.oulib.exception.AppException;
import com.ou.oulib.security.CustomJwtDecoder;
import com.ou.oulib.security.JwtCookieAuthenticationFilter;
import com.ou.oulib.service.UserService;
import com.ou.oulib.utils.PageResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private CustomJwtDecoder customJwtDecoder;

    @MockitoBean
    private JwtCookieAuthenticationFilter jwtCookieAuthenticationFilter;

    @MockitoBean
    private JwtAuthenticationConverter jwtAuthenticationConverter;

    @Test
    @DisplayName("POST /api/v1/users: should return 201 when request is valid")
    void shouldCreateUser_whenRequestValid() throws Exception {
        UserCreationRequest request = UserCreationRequest.builder()
                .email("alice@example.com")
                .fullName("Alice")
                .password("secret123")
                .build();
        UserResponse created = UserResponse.builder()
                .id("u-1")
                .email("alice@example.com")
                .fullName("Alice")
                .role("USER")
                .status("ACTIVE")
                .build();

        when(userService.createUser(any(UserCreationRequest.class))).thenReturn(created);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.message").value("Created"))
                .andExpect(jsonPath("$.result.id").value("u-1"))
                .andExpect(jsonPath("$.result.email").value("alice@example.com"));
    }

    @Test
    @DisplayName("POST /api/v1/users: should return 400 when payload is invalid")
    void shouldReturnBadRequest_whenCreateUserInputInvalid() throws Exception {
        UserCreationRequest request = UserCreationRequest.builder()
                .email("")
                .fullName("")
                .password("123")
                .build();

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Validation Failed"))
                .andExpect(jsonPath("$.result.email").exists());

        verify(userService, never()).createUser(any(UserCreationRequest.class));
    }

    @Test
    @DisplayName("GET /api/v1/users: should return paginated users")
    void shouldReturnUsers_whenGetUsersCalled() throws Exception {
        UserResponse user = UserResponse.builder()
                .id("u-1")
                .email("alice@example.com")
                .fullName("Alice")
                .build();
        PageResponse<UserResponse> page = PageResponse.<UserResponse>builder()
                .content(List.of(user))
                .pageNumber(0)
                .pageSize(10)
                .totalElements(1)
                .totalPages(1)
                .first(true)
                .last(true)
                .empty(false)
                .build();

        when(userService.getUsers(0, 10)).thenReturn(page);

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.result.content[0].id").value("u-1"))
                .andExpect(jsonPath("$.result.totalElements").value(1));
    }

    @Test
    @DisplayName("PATCH /api/v1/users/{id}/status: should update user status")
    void shouldUpdateUserStatus_whenRequestValid() throws Exception {
        UserStatusUpdateRequest request = UserStatusUpdateRequest.builder()
                .status(UserStatus.SUSPENDED)
                .build();
        UserResponse updated = UserResponse.builder()
                .id("u-1")
                .status("SUSPENDED")
                .build();

        when(userService.updateUserStatus(eq("u-1"), any(UserStatusUpdateRequest.class))).thenReturn(updated);

        mockMvc.perform(patch("/api/v1/users/u-1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.result.id").value("u-1"))
                .andExpect(jsonPath("$.result.status").value("SUSPENDED"));
    }

    @Test
    @DisplayName("PATCH /api/v1/users/{id}/status: should return 404 when user is not found")
    void shouldReturnNotFound_whenUserStatusTargetMissing() throws Exception {
        UserStatusUpdateRequest request = UserStatusUpdateRequest.builder()
                .status(UserStatus.SUSPENDED)
                .build();

        when(userService.updateUserStatus(eq("missing"), any(UserStatusUpdateRequest.class)))
                .thenThrow(new AppException(ErrorCode.USER_NOT_FOUND));

        mockMvc.perform(patch("/api/v1/users/missing/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(1001))
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    @DisplayName("GET /api/v1/users/me: should return 500 for uncategorized runtime exception")
    void shouldReturnInternalServerError_whenUnhandledExceptionOccurs() throws Exception {
        Jwt jwt = new Jwt(
                "test-token",
                Instant.now(),
                Instant.now().plusSeconds(300),
                Map.of("alg", "none"),
                Map.of("sub", "user-1")
        );
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt));
        when(userService.getCurrentUser(nullable(Jwt.class))).thenThrow(new RuntimeException("boom"));

        try {
            mockMvc.perform(get("/api/v1/users/me"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.code").value(1000))
                    .andExpect(jsonPath("$.message").value("Uncategorized Error"));
        } finally {
            SecurityContextHolder.clearContext();
        }
    }
}
