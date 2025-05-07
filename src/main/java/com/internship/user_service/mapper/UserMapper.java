package com.internship.user_service.mapper;

import com.internship.user_service.constants.FilePath;
import com.internship.user_service.model.User;
import com.internship.user_service.dto.UserDTO;
import com.internship.user_service.dto.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Value;

@Mapper(componentModel = "spring",
        imports = FilePath.class,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class UserMapper {

    @Value("${gcs.bucket.name}")
    private String bucketName;

    @Mapping(target = "profilePicturePath", expression = "java(buildFullGcsUrl(user.getProfilePicturePath()))")
    public abstract UserResponse toUserResponse(User user);

    public abstract User toUserEntity(UserDTO userDTO);

    @Named("buildFullGcsUrl")
    protected String buildFullGcsUrl(String path) {
        return "https://storage.googleapis.com/" + bucketName + "/" + path;
    }
}
