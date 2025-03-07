package com.internship.user_service.service;

import com.internship.user_service.dto.UserDTO;
import com.internship.user_service.dto.UserResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    UserResponse createUser(UserDTO userDTO);

    UserResponse addProfilePicture(Long id, MultipartFile file);

    UserResponse getUser(Long id);

    List<UserResponse> getAllUsers();
}
