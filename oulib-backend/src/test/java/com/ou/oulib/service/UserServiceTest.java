//package com.ou.oulib.service;
//
//import com.cloudinary.Cloudinary;
//import com.ou.oulib.dto.request.ChangePasswordRequest;
//import com.ou.oulib.dto.request.UserCreationRequest;
//import com.ou.oulib.dto.request.UserStatusUpdateRequest;
//import com.ou.oulib.dto.request.UserUpdateRequest;
//import com.ou.oulib.dto.response.UserResponse;
//import com.ou.oulib.entity.User;
//import com.ou.oulib.enums.ErrorCode;
//import com.ou.oulib.enums.UserRole;
//import com.ou.oulib.enums.UserStatus;
//import com.ou.oulib.exception.AppException;
//import com.ou.oulib.mapper.UserMapper;
//import com.ou.oulib.repository.UserRepository;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.dao.DataIntegrityViolationException;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.oauth2.jwt.Jwt;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertSame;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.any;
//import static org.mockito.Mockito.eq;
//import static org.mockito.Mockito.never;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class UserServiceTest {
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private UserMapper userMapper;
//
//    @Mock
//    private PasswordEncoder passwordEncoder;
//
//    @Mock
//    private Cloudinary cloudinary;
//
//    @InjectMocks
//    private UserService userService;
//
//    @Test
//    @DisplayName("createUser: should create user when request is valid")
//    void shouldCreateUser_whenRequestIsValid() {
//        UserCreationRequest request = UserCreationRequest.builder()
//                .email("alice@example.com")
//                .fullName("Alice")
//                .password("secret123")
//                .build();
//        User user = User.builder()
//                .email(request.getEmail())
//                .fullName(request.getFullName())
//                .build();
//        UserResponse expected = UserResponse.builder()
//                .id("u-1")
//                .email(request.getEmail())
//                .fullName(request.getFullName())
//                .role("USER")
//                .build();
//
//        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
//        when(userMapper.toUser(request)).thenReturn(user);
//        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded-password");
//        when(userMapper.toResponse(user)).thenReturn(expected);
//
//        UserResponse actual = userService.createUser(request);
//
//        assertSame(expected, actual);
//        assertEquals(UserRole.USER, user.getRole());
//        assertEquals("encoded-password", user.getPassword());
//        verify(userRepository).save(user);
//    }
//
//    @Test
//    @DisplayName("createUser: should throw USER_ALREADY_EXISTED when email already exists")
//    void shouldThrowException_whenEmailAlreadyExists() {
//        UserCreationRequest request = UserCreationRequest.builder()
//                .email("alice@example.com")
//                .password("secret123")
//                .fullName("Alice")
//                .build();
//
//        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);
//
//        AppException exception = assertThrows(AppException.class, () -> userService.createUser(request));
//
//        assertEquals(ErrorCode.USER_ALREADY_EXISTED, exception.getErrorCode());
//        verify(userRepository, never()).save(any(User.class));
//    }
//
//    @Test
//    @DisplayName("createUser: should map DataIntegrityViolationException to USER_ALREADY_EXISTED")
//    void shouldThrowException_whenSaveViolatesUniqueConstraint() {
//        UserCreationRequest request = UserCreationRequest.builder()
//                .email("alice@example.com")
//                .fullName("Alice")
//                .password("secret123")
//                .build();
//        User user = User.builder().email(request.getEmail()).build();
//
//        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
//        when(userMapper.toUser(request)).thenReturn(user);
//        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded-password");
//        when(userRepository.save(user)).thenThrow(new DataIntegrityViolationException("duplicate"));
//
//        AppException exception = assertThrows(AppException.class, () -> userService.createUser(request));
//
//        assertEquals(ErrorCode.USER_ALREADY_EXISTED, exception.getErrorCode());
//    }
//
//    @Test
//    @DisplayName("updateUserStatus: should update status when user id exists")
//    void shouldReturnUpdatedUser_whenIdExists() {
//        String userId = "u-1";
//        User user = User.builder().id(userId).status(UserStatus.ACTIVE).build();
//        UserStatusUpdateRequest request = UserStatusUpdateRequest.builder()
//                .status(UserStatus.SUSPENDED)
//                .build();
//        UserResponse expected = UserResponse.builder().id(userId).status("SUSPENDED").build();
//
//        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//        when(userMapper.toResponse(user)).thenReturn(expected);
//
//        UserResponse actual = userService.updateUserStatus(userId, request);
//
//        assertSame(expected, actual);
//        assertEquals(UserStatus.SUSPENDED, user.getStatus());
//        verify(userRepository).save(user);
//    }
//
//    @Test
//    @DisplayName("updateUserStatus: should throw USER_NOT_FOUND when user id does not exist")
//    void shouldThrowException_whenUserNotFound() {
//        String userId = "missing-user";
//        UserStatusUpdateRequest request = UserStatusUpdateRequest.builder()
//                .status(UserStatus.SUSPENDED)
//                .build();
//
//        when(userRepository.findById(userId)).thenReturn(Optional.empty());
//
//        AppException exception = assertThrows(AppException.class, () -> userService.updateUserStatus(userId, request));
//
//        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
//        verify(userRepository, never()).save(any(User.class));
//    }
//
//    @Test
//    @DisplayName("getCurrentUser: should return current user when jwt subject exists")
//    void shouldReturnCurrentUser_whenJwtSubjectExists() {
//        String email = "alice@example.com";
//        Jwt jwt = Jwt.withTokenValue("token")
//                .header("alg", "none")
//                .subject(email)
//                .claim("sub", email)
//                .build();
//        User user = User.builder().email(email).build();
//        UserResponse expected = UserResponse.builder().email(email).build();
//
//        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
//        when(userMapper.toResponse(user)).thenReturn(expected);
//
//        UserResponse actual = userService.getCurrentUser(jwt);
//
//        assertSame(expected, actual);
//        verify(userRepository).findByEmail(email);
//    }
//
//    @Test
//    @DisplayName("updateMyProfile: should save user when full name changed")
//    void shouldUpdateProfile_whenFullNameChanged() {
//        String email = "alice@example.com";
//        Jwt jwt = Jwt.withTokenValue("token")
//                .header("alg", "none")
//                .subject(email)
//                .build();
//        User user = User.builder().email(email).fullName("Alice Old").build();
//        UserUpdateRequest request = UserUpdateRequest.builder().fullName("Alice New").build();
//        UserResponse expected = UserResponse.builder().fullName("Alice New").build();
//
//        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
//        when(userMapper.toResponse(user)).thenReturn(expected);
//
//        UserResponse actual = userService.updateMyProfile(jwt, request, null);
//
//        assertSame(expected, actual);
//        assertEquals("Alice New", user.getFullName());
//        verify(userRepository).save(user);
//    }
//
//    @Test
//    @DisplayName("changePassword: should throw UNAUTHENTICATED when old password does not match")
//    void shouldThrowException_whenOldPasswordIsIncorrect() {
//        String email = "alice@example.com";
//        Jwt jwt = Jwt.withTokenValue("token")
//                .header("alg", "none")
//                .subject(email)
//                .build();
//        User user = User.builder().email(email).password("encoded-old").build();
//        ChangePasswordRequest request = ChangePasswordRequest.builder()
//                .oldPassword("wrong-old")
//                .newPassword("new-password")
//                .build();
//
//        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
//        when(passwordEncoder.matches(request.getOldPassword(), user.getPassword())).thenReturn(false);
//
//        AppException exception = assertThrows(AppException.class, () -> userService.changePassword(jwt, request));
//
//        assertEquals(ErrorCode.UNAUTHENTICATED, exception.getErrorCode());
//        verify(userRepository, never()).save(any(User.class));
//    }
//
//    @Test
//    @DisplayName("changePassword: should update password when old password is correct")
//    void shouldUpdatePassword_whenOldPasswordMatches() {
//        String email = "alice@example.com";
//        Jwt jwt = Jwt.withTokenValue("token")
//                .header("alg", "none")
//                .subject(email)
//                .build();
//        User user = User.builder().email(email).password("encoded-old").build();
//        ChangePasswordRequest request = ChangePasswordRequest.builder()
//                .oldPassword("old-password")
//                .newPassword("new-password")
//                .build();
//
//        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
//        when(passwordEncoder.matches(request.getOldPassword(), user.getPassword())).thenReturn(true);
//        when(passwordEncoder.encode(request.getNewPassword())).thenReturn("encoded-new");
//
//        userService.changePassword(jwt, request);
//
//        assertEquals("encoded-new", user.getPassword());
//        verify(userRepository).save(user);
//        verify(passwordEncoder).encode(request.getNewPassword());
//    }
//
//    @Test
//    @DisplayName("getCurrentUser: should throw USER_NOT_FOUND when jwt subject does not exist")
//    void shouldThrowException_whenCurrentUserNotFound() {
//        String email = "missing@example.com";
//        Jwt jwt = Jwt.withTokenValue("token")
//                .header("alg", "none")
//                .subject(email)
//                .claim("sub", email)
//                .build();
//
//        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
//
//        AppException exception = assertThrows(AppException.class, () -> userService.getCurrentUser(jwt));
//
//        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
//    }
//
//    @Test
//    @DisplayName("updateMyProfile: should not save when no field changes")
//    void shouldNotSave_whenProfileDataUnchanged() {
//        String email = "alice@example.com";
//        Jwt jwt = Jwt.withTokenValue("token")
//                .header("alg", "none")
//                .subject(email)
//                .build();
//        User user = User.builder().email(email).fullName("Alice").build();
//        UserUpdateRequest request = UserUpdateRequest.builder().fullName("Alice").build();
//        UserResponse expected = UserResponse.builder().fullName("Alice").build();
//
//        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
//        when(userMapper.toResponse(user)).thenReturn(expected);
//
//        UserResponse actual = userService.updateMyProfile(jwt, request, null);
//
//        assertSame(expected, actual);
//        verify(userRepository, never()).save(any(User.class));
//    }
//}
