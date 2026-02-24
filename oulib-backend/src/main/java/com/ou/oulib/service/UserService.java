package com.ou.oulib.service;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.ou.oulib.dto.request.UserCreationRequest;
import com.ou.oulib.dto.request.UserUpdateRequest;
import com.ou.oulib.dto.response.UserResponse;
import com.ou.oulib.entity.User;
import com.ou.oulib.enums.ErrorCode;
import com.ou.oulib.enums.UserRole;
import com.ou.oulib.enums.UserStatus;
import com.ou.oulib.exception.AppException;
import com.ou.oulib.mapper.UserMapper;
import com.ou.oulib.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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


    @Transactional
    public UserResponse createUser(UserCreationRequest userCreation) {
        if (this.userRepository.existsByEmail(userCreation.getEmail())) {
            throw new AppException(ErrorCode.USER_ALREADY_EXISTED);
        }
        User user = this.userMapper.toUser(userCreation);
        user.setRole(UserRole.USER);
        user.setStatus(UserStatus.ACTIVE);
        user.setActive(true);
        user.setPassword(this.passwordEncoder.encode(userCreation.getPassword()));

        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            throw new AppException(ErrorCode.USER_ALREADY_EXISTED);
        }
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
        return userMapper.toResponse(user);
    }


}
