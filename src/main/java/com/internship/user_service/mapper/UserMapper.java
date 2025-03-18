package com.internship.user_service.mapper;

import com.internship.user_service.model.User;
import com.internship.user_service.dto.UserDTO;
import com.internship.user_service.dto.UserResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toUserResponse(User user);

    User toUserEntity(UserDTO userDTO);
}
