package com.internship.user_service.mapper;

import com.internship.user_service.constants.FilePath;
import com.internship.user_service.model.User;
import com.internship.user_service.dto.UserDTO;
import com.internship.user_service.dto.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        imports = FilePath.class)
public interface UserMapper {

    @Mapping(target = "profilePicturePath",
            expression = "java(user.getProfilePicturePath() != null ? FilePath.PATH_PICTURE_URL + user.getProfilePicturePath() : null)")
    UserResponse toUserResponse(User user);

    User toUserEntity(UserDTO userDTO);
}
