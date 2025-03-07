package com.internship.user_service.service;

import com.internship.user_service.constants.FilePath;
import com.internship.user_service.exception.PictureNotFoundException;
import com.internship.user_service.exception.UserNotFoundException;
import com.internship.user_service.mapper.UserMapper;
import com.internship.user_service.model.User;
import com.internship.user_service.model.enums.Status;
import com.internship.user_service.dto.UserDTO;
import com.internship.user_service.repository.UserRepository;
import com.internship.user_service.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private final String uploadDir = System.getProperty("user.dir") + FilePath.PATH;

    @Override
    public UserResponse createUser(UserDTO userDTO) {
        userDTO.setStatus(Status.ACTIVE);
        userDTO.setVerified(false);
        User user = userRepository.save(userMapper.toUserEntity(userDTO));
        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse addProfilePicture(Long userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if(file == null || file.isEmpty()) {
            throw new UserNotFoundException("Profile picture is missing!");
        }

        String fileName = "pictureUserId_" + userId + ".jpg";
        Path filePath = Paths.get(uploadDir).resolve(fileName);

        try {
            file.transferTo(filePath.toFile());
        }
        catch (IOException e) {
            throw new PictureNotFoundException("IO Exception occurred while uploading profile picture!");
        }

        user.setProfilePicturePath(fileName);
        User savedUser = userRepository.save(user);

        return userMapper.toUserResponse(savedUser);
    }

    @Override
    public UserResponse getUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        UserResponse userResponse = userMapper.toUserResponse(user);
        userResponse.setProfilePicturePath(FilePath.PATH_PICTURE_URL + user.getProfilePicturePath());
        return userResponse;
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository
                .findAll()
                .stream().map(userMapper::toUserResponse).toList();
    }

}
