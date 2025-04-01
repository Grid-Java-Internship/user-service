package com.internship.user_service.service;

import com.internship.user_service.constants.FilePath;
import com.internship.user_service.exception.PictureNotFoundException;
import com.internship.user_service.exception.UserAlreadyExistsException;
import com.internship.user_service.exception.UserNotFoundException;
import com.internship.user_service.mapper.UserMapper;
import com.internship.user_service.model.User;
import com.internship.user_service.model.enums.Status;
import com.internship.user_service.dto.UserDTO;
import com.internship.user_service.repository.UserRepository;
import com.internship.user_service.dto.UserResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static com.internship.user_service.constants.FilePath.ALLOWED_EXTENSIONS;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final String uploadDir = System.getProperty("user.dir") + FilePath.PATH;

    private String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    boolean isValidImageExtension(String fileName) {
        String extension = getFileExtension(fileName);
        return ALLOWED_EXTENSIONS.contains(extension);
    }

    @Override
    public UserResponse createUser(UserDTO userDTO) {
        if(userRepository.existsById(userDTO.getId())) {
            log.error("User with id {} already exists.", userDTO.getId());
            throw new UserAlreadyExistsException("User with id " + userDTO.getId() + " already exists.");
        }
        userDTO.setStatus(Status.ACTIVE);
        userDTO.setVerified(false);
        User user = userRepository.save(userMapper.toUserEntity(userDTO));
        log.info("User with id {} created successfully.", user.getId());
        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse addProfilePicture(Long userId, MultipartFile file) {
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> {
                    log.error("User with id {} not found.", userId);
                    return new UserNotFoundException("User not found.");
                });

        if(file == null || file.isEmpty()) {
            log.error("Profile picture is missing!");
            throw new PictureNotFoundException("Profile picture is missing!");
        }

        String originalFilename = file.getOriginalFilename();

        if (originalFilename == null || !isValidImageExtension(originalFilename)) {
            log.error("Invalid file type!");
            throw new PictureNotFoundException("Invalid file type!");
        }

        String fileName = "pictureUserId_" + userId + "." + getFileExtension(originalFilename);
        Path filePath = Paths.get(uploadDir).resolve(fileName);

        try {
            file.transferTo(filePath.toFile());
        }
        catch (IOException e) {
            log.error("IO Exception occurred while uploading profile picture!");
            throw new PictureNotFoundException("IO Exception occurred while uploading profile picture!");
        }

        user.setProfilePicturePath(fileName);
        User savedUser = userRepository.save(user);
        log.info("Profile picture added for user with id {}.", userId);
        return userMapper.toUserResponse(savedUser);
    }

    @Override
    public UserResponse getUser(Long userId) {
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> {
                    log.error("User with id {} not found.", userId);
                    return new UserNotFoundException("User not found.");
                });
        UserResponse userResponse = userMapper.toUserResponse(user);
        userResponse.setProfilePicturePath(FilePath.PATH_PICTURE_URL + user.getProfilePicturePath());
        log.info("Retrieved user with id {}.", userId);
        return userResponse;
    }

    @Override
    public List<UserResponse> getAllUsers() {
        List<UserResponse> users = userRepository
                .findAll()
                .stream()
                .map(userMapper::toUserResponse)
                .toList();
        log.info("Retrieved all users. Total count: {}.", users.size());
        return users;
    }

    @Override
    public Boolean undoUserCreation(Long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
            new UserNotFoundException("User not found.")
        );

        userRepository.delete(user);
        return true;
    }

    @Override
    @Transactional
    public UserResponse editUser(UserDTO userDTO) {

        if (userDTO.getId() == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        User user = userRepository
                .findById(userDTO.getId())
                .orElseThrow(() -> {
                    log.error("User with id {} not found.", userDTO.getId());
                    return new UserNotFoundException("User not found.");
                });

        user.setName(userDTO.getName());
        user.setSurname(userDTO.getSurname());
        user.setBirthday(userDTO.getBirthday());
        user.setAddress(userDTO.getAddress());
        user.setPhone(userDTO.getPhone());
        user.setCountry(userDTO.getCountry());
        user.setCity(userDTO.getCity());
        user.setZipCode(userDTO.getZipCode());

        log.info("User with id {} updated successfully.", user.getId());
        return userMapper.toUserResponse(user);
    }

}
