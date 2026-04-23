package com.ou.oulib.service;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.ou.oulib.dto.request.UserCreationRequest;
import com.ou.oulib.dto.request.ChangePasswordRequest;
import com.ou.oulib.dto.request.StaffCreationRequest;
import com.ou.oulib.dto.request.UserStatusUpdateRequest;
import com.ou.oulib.dto.request.UserUpdateRequest;
import com.ou.oulib.dto.response.UserResponse;
import com.ou.oulib.entity.User;
import com.ou.oulib.enums.AuditAction;
import com.ou.oulib.enums.ErrorCode;
import com.ou.oulib.enums.ResourceType;
import com.ou.oulib.enums.UserRole;
import com.ou.oulib.enums.UserStatus;
import com.ou.oulib.exception.AppException;
import com.ou.oulib.infras.event.AuditMessage;
import com.ou.oulib.infras.producer.AuditProducer;
import com.ou.oulib.mapper.UserMapper;
import com.ou.oulib.repository.UserRepository;
import com.ou.oulib.utils.PageResponse;
import com.ou.oulib.utils.PageResponseUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    Cloudinary cloudinary;
    AuditProducer auditProducer;


    @Transactional
    public UserResponse createUser(UserCreationRequest userCreation) {
        if (this.userRepository.existsByEmail(userCreation.getEmail())) {
            throw new AppException(ErrorCode.USER_ALREADY_EXISTED);
        }
        User user = this.userMapper.toUser(userCreation);
        user.setRole(UserRole.USER);
        user.setPassword(this.passwordEncoder.encode(userCreation.getPassword()));

        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            throw new AppException(ErrorCode.USER_ALREADY_EXISTED);
        }

        auditProducer.sendAuditLog(AuditMessage.builder()
                .userId(parseToLongOrDefault(user.getId(), 0L))
                .action(AuditAction.CREATE.name())
                .resourceType(ResourceType.USER.name())
                .resourceId(parseToLong(user.getId()))
                .newValue("{\"email\":\"" + user.getEmail() + "\",\"role\":\"" + user.getRole() + "\"}")
                .timestamp(Instant.now())
                .build());

        return userMapper.toResponse(user);
    }

    @Transactional
    @PreAuthorize("hasRole('SYSADMIN')")
    public UserResponse createLibrarian(UserCreationRequest userCreation) {
        if (this.userRepository.existsByEmail(userCreation.getEmail())) {
            throw new AppException(ErrorCode.USER_ALREADY_EXISTED);
        }
        User user = this.userMapper.toUser(userCreation);
        user.setRole(UserRole.LIBRARIAN);
        user.setPassword(this.passwordEncoder.encode(userCreation.getPassword()));

        userRepository.save(user);
        return userMapper.toResponse(user);
    }

    @Transactional
    @PreAuthorize("hasRole('SYSADMIN')")
    public UserResponse createStaff(StaffCreationRequest request) {
        if (this.userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.USER_ALREADY_EXISTED);
        }

        if (request.getRole() != UserRole.SYSADMIN && request.getRole() != UserRole.LIBRARIAN) {
            throw new AppException(ErrorCode.INVALID_ROLE);
        }

        User user = User.builder()
                .email(request.getEmail())
                .fullName(request.getFullName())
                .role(request.getRole())
                .password(this.passwordEncoder.encode(request.getPassword()))
                .build();

        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            throw new AppException(ErrorCode.USER_ALREADY_EXISTED);
        }

        auditProducer.sendAuditLog(AuditMessage.builder()
                .userId(getCurrentActorUserId())
                .action(AuditAction.CREATE.name())
                .resourceType(ResourceType.USER.name())
                .resourceId(parseToLong(user.getId()))
                .newValue("{\"email\":\"" + user.getEmail() + "\",\"role\":\"" + user.getRole() + "\"}")
                .timestamp(Instant.now())
                .build());

        return userMapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('SYSADMIN','LIBRARIAN')")
    public PageResponse<UserResponse> getUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = userRepository.findAll(pageable);
        return PageResponseUtils.build(users, userMapper::toResponse);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('SYSADMIN','LIBRARIAN')")
    public UserResponse updateUserStatus(String userId, UserStatusUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        UserStatus oldStatus = user.getStatus();
        user.setStatus(request.getStatus());
        userRepository.save(user);

        auditProducer.sendAuditLog(AuditMessage.builder()
            .userId(getCurrentActorUserId())
            .action(AuditAction.UPDATE.name())
            .resourceType(ResourceType.USER.name())
            .resourceId(parseToLong(user.getId()))
            .oldValue("{\"status\":\"" + oldStatus + "\"}")
            .newValue("{\"status\":\"" + user.getStatus() + "\"}")
            .timestamp(Instant.now())
            .build());

        return userMapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(Jwt jwt) {
        String userEmail = jwt.getClaimAsString("sub");
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toResponse(user);
    }


    @Transactional
    public UserResponse updateMyProfile(Jwt jwt, UserUpdateRequest request, MultipartFile avatar) {
        String userEmail = jwt.getSubject();

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        String oldFullName = user.getFullName();
        boolean isUpdated = false;
        if (request.getFullName() != null &&
                !request.getFullName().equals(user.getFullName())) {
            user.setFullName(request.getFullName());
            isUpdated = true;
        }


        if (!isUpdated) {
            return userMapper.toResponse(user);
        }
        userRepository.save(user);

        auditProducer.sendAuditLog(AuditMessage.builder()
                .userId(parseToLongOrDefault(user.getId(), 0L))
                .action(AuditAction.UPDATE.name())
                .resourceType(ResourceType.USER.name())
                .resourceId(parseToLong(user.getId()))
                .oldValue("{\"fullName\":\"" + oldFullName + "\"}")
                .newValue("{\"fullName\":\"" + user.getFullName() + "\"}")
                .timestamp(Instant.now())
                .build());

        return userMapper.toResponse(user);
    }

    @Transactional
    public void changePassword(Jwt jwt, ChangePasswordRequest request) {
        String userEmail = jwt.getSubject();

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.OLD_PASSWORD_MISMATCH);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        auditProducer.sendAuditLog(AuditMessage.builder()
                .userId(parseToLongOrDefault(user.getId(), 0L))
                .action(AuditAction.UPDATE.name())
                .resourceType(ResourceType.USER.name())
                .resourceId(parseToLong(user.getId()))
                .newValue("{\"passwordChanged\":true}")
                .timestamp(Instant.now())
                .build());
    }

    private Long getCurrentActorUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return 0L;
        }

        String email = null;
        Object principal = authentication.getPrincipal();
        if (principal instanceof Jwt jwt) {
            email = jwt.getSubject();
        }

        if (email == null || email.isBlank()) {
            email = authentication.getName();
        }

        if (email == null || email.isBlank()) {
            return 0L;
        }

        return userRepository.findByEmail(email)
                .map(user -> parseToLongOrDefault(user.getId(), 0L))
                .orElse(0L);
    }

    private Long parseToLong(String rawId) {
        if (rawId == null || rawId.isBlank()) {
            return null;
        }
        try {
            return Long.valueOf(rawId);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private Long parseToLongOrDefault(String rawId, Long defaultValue) {
        Long value = parseToLong(rawId);
        return value != null ? value : defaultValue;
    }


}
