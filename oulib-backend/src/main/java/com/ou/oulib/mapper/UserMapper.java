package com.ou.oulib.mapper;

import com.ou.oulib.dto.request.UserCreationRequest;
import com.ou.oulib.dto.response.UserResponse;
import com.ou.oulib.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(UserCreationRequest userCreationRequest);

//    @Mapping(source = "role", target = "role")
    UserResponse toResponse(User user);
}
