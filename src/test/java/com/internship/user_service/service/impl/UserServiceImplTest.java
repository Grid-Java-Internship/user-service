package com.internship.user_service.service.impl;

import com.internship.user_service.constants.FilePath;
import com.internship.user_service.dto.UserDTO;
import com.internship.user_service.dto.UserResponse;
import com.internship.user_service.exception.PictureNotFoundException;
import com.internship.user_service.exception.AlreadyExistsException;
import com.internship.user_service.exception.UserNotFoundException;
import com.internship.user_service.mapper.UserMapper;
import com.internship.user_service.model.User;
import com.internship.user_service.model.enums.Status;
import com.internship.user_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

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

        userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setName("Marko");
        userDTO.setSurname("Markovic");
        userDTO.setEmail("marko@internship.com");

        userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setName("Marko");
        userResponse.setSurname("Markovic");
        userResponse.setEmail("marko@internship.com");
    }

    @Test
    void createUser() {
        user.setStatus(Status.ACTIVE);
        user.setVerified(false);
        when(userMapper.toUserEntity(userDTO)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.createUser(userDTO);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Marko", result.getName());
        assertEquals("Markovic", result.getSurname());
        assertEquals("marko@internship.com", result.getEmail());

        ArgumentCaptor<UserDTO> userDTOCaptor = ArgumentCaptor.forClass(UserDTO.class);
        verify(userMapper).toUserEntity(userDTOCaptor.capture());
        UserDTO capturedUserDTO = userDTOCaptor.getValue();
        assertEquals(Status.ACTIVE, capturedUserDTO.getStatus());
        assertFalse(capturedUserDTO.getVerified());

        verify(userMapper, times(1)).toUserEntity(userDTO);
        verify(userRepository, times(1)).save(user);
        verify(userMapper, times(1)).toUserResponse(user);
    }

    @Test
    void createUserWhenUserAlreadyExists() {
        when(userRepository.existsById(1L)).thenReturn(true);
        AlreadyExistsException exception = assertThrows(AlreadyExistsException.class, () -> userService.createUser(userDTO));
        assertEquals("User with id 1 already exists.", exception.getMessage());

        verify(userRepository, times(1)).existsById(1L);
        verify(userMapper, never()).toUserEntity(any());
        verify(userRepository, never()).save(any());
        verify(userMapper, never()).toUserResponse(any());
    }

    @Test
    void getUserWhenExists() {
        user.setProfilePicturePath("pictureUserId_12");
        userResponse.setProfilePicturePath(FilePath.PATH_PICTURE_URL + user.getProfilePicturePath());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.getUser(1L);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Marko", result.getName());
        assertEquals("Markovic", result.getSurname());
        assertEquals("marko@internship.com", result.getEmail());
        assertEquals(FilePath.PATH_PICTURE_URL + "pictureUserId_12", result.getProfilePicturePath());

        verify(userRepository, times(1)).findById(1L);
        verify(userMapper, times(1)).toUserResponse(user);

    }

    @Test
    void getUserWhenDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.getUser(1L));
        assertEquals("User not found.", exception.getMessage());

        verify(userRepository, times(1)).findById(1L);
        verify(userMapper, never()).toUserResponse(any());
    }

    @Test
    void getAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        List<UserResponse> users = userService.getAllUsers();

        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals("Marko", users.get(0).getName());
        assertEquals("Markovic", users.get(0).getSurname());
        assertEquals("marko@internship.com", users.get(0).getEmail());

        verify(userRepository, times(1)).findAll();
        verify(userMapper, times(1)).toUserResponse(user);
    }

    @Test
    void getAllUsersWhenEmptyList() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        List<UserResponse> users = userService.getAllUsers();

        assertNotNull(users);
        assertEquals(0, users.size());

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void addProfilePictureWhenUserNotFound() {
        MockMultipartFile file = new MockMultipartFile("file", "profile.jpg",
                "image/jpeg", new byte[10]);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.addProfilePicture(1L, file));
        assertEquals("User not found.", exception.getMessage());

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, never()).save(any());
        verify(userMapper, never()).toUserResponse(any());
    }

    @Test
    void addProfilePictureWhenNullOrEmpty() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        MultipartFile emptyFile = new MockMultipartFile("file", "profile.jpg",
                "image/jpeg", new byte[0]);

        PictureNotFoundException firstException = assertThrows(PictureNotFoundException.class, () -> userService.addProfilePicture(1L, null));
        PictureNotFoundException secondException = assertThrows(PictureNotFoundException.class, () -> userService.addProfilePicture(1L, emptyFile));
        assertEquals("Profile picture is missing!", firstException.getMessage());
        assertEquals("Profile picture is missing!", secondException.getMessage());

        verify(userRepository, times(2)).findById(1L);
        verify(userRepository, never()).save(any());
        verify(userMapper, never()).toUserResponse(any());
    }

    @Test
    void addProfilePictureWhenIOException() throws IOException {
        MultipartFile mockIOExceptionFile = mock(MultipartFile.class);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(mockIOExceptionFile.getOriginalFilename()).thenReturn("valid-image.jpg");
        doThrow(new IOException()).when(mockIOExceptionFile).transferTo(any(File.class));

        PictureNotFoundException exception = assertThrows(PictureNotFoundException.class,
                () -> userService.addProfilePicture(1L, mockIOExceptionFile));
        assertEquals("IO Exception occurred while uploading profile picture!", exception.getMessage());

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, never()).save(any());
        verify(userMapper, never()).toUserResponse(any());
        verify(mockIOExceptionFile, times(1)).transferTo(any(File.class));
        verify(mockIOExceptionFile, times(1)).getOriginalFilename();

    }

    @Test
    void addProfilePictureWhenInvalidFileExtensionOrNull() {
        MultipartFile mockInvalidFileExtension = mock(MultipartFile.class);
        MultipartFile mockNullFile = mock(MultipartFile.class);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(mockInvalidFileExtension.getOriginalFilename()).thenReturn("invalid-image.pdf");
        when(mockNullFile.getOriginalFilename()).thenReturn(null);

        PictureNotFoundException firstException = assertThrows(PictureNotFoundException.class,
                () -> userService.addProfilePicture(1L, mockInvalidFileExtension));
        assertEquals("Invalid file type!", firstException.getMessage());

        PictureNotFoundException secondException = assertThrows(PictureNotFoundException.class,
                () -> userService.addProfilePicture(1L, mockNullFile));
        assertEquals("Invalid file type!", secondException.getMessage());

        verify(userRepository, times(2)).findById(1L);
        verify(userRepository, never()).save(any());
        verify(userMapper, never()).toUserResponse(any());
        verify(mockInvalidFileExtension, times(1)).getOriginalFilename();
        verify(mockNullFile, times(1)).getOriginalFilename();
    }


    @Test
    void addProfilePictureWhenSuccessful() throws IOException {
        MultipartFile mockFile = mock(MultipartFile.class);
        userResponse.setProfilePicturePath("pictureUserId_1.jpg");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);
        when(mockFile.getOriginalFilename()).thenReturn("valid-image.jpg");
        doNothing().when(mockFile).transferTo(any(File.class));

        UserResponse result = userService.addProfilePicture(1L, mockFile);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Marko", result.getName());
        assertEquals("Markovic", result.getSurname());
        assertEquals("marko@internship.com", result.getEmail());
        assertEquals("pictureUserId_1.jpg", result.getProfilePicturePath());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();
        assertEquals("pictureUserId_1.jpg", capturedUser.getProfilePicturePath());

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(user);
        verify(userMapper, times(1)).toUserResponse(user);
        verify(mockFile, times(1)).transferTo(any(File.class));
        verify(mockFile, times(1)).getOriginalFilename();

    }

    @Test
    void undoUserCreation_shouldReturnTrue_whenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);

        boolean result = userService.undoUserCreation(1L);

        assertTrue(result);

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void undoUserCreation_shouldThrowException_whenUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.undoUserCreation(1L)
        );

        assertNotNull(exception);
        assertEquals("User not found.", exception.getMessage());
    }

    @Test
    void getUserEntity_shouldReturnUserEntity() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getUserEntity(1L);

        assertEquals(user, result);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getUserEntity_shouldThrowUserNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserEntity(1L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found.");

        verifyNoMoreInteractions(userRepository);
    }
}