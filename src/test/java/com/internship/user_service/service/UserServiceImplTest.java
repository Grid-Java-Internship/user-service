package com.internship.user_service.service;

import com.internship.user_service.dto.UserDTO;
import com.internship.user_service.dto.UserResponse;
import com.internship.user_service.mapper.UserMapper;
import com.internship.user_service.model.User;
import com.internship.user_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDTO userDTO;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Marko");
        user.setSurname("Markovic");
        user.setEmail("marko@internship.com");
        user.setProfilePicturePath("uploads/profile_pictures/pictureUserId_1.jpg");

        userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setName("Markovic");
        userDTO.setSurname("Markovic");

    }

    @Test
    void createUser() {
    }

    @Test
    void addProfilePicture() {
    }

    @Test
    void getUser() {
    }

    @Test
    void getAllUsers() {
    }
}