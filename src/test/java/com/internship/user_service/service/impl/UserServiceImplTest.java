package com.internship.user_service.service.impl;

import com.internship.user_service.constants.FilePath;
import com.internship.user_service.dto.AvailabilityDTO;
import com.internship.user_service.dto.UserDTO;
import com.internship.user_service.dto.UserResponse;
import com.internship.user_service.dto.WorkingHoursRequest;
import com.internship.user_service.exception.*;
import com.internship.user_service.mapper.AvailabilityMapper;
import com.internship.user_service.exception.PictureNotFoundException;
import com.internship.user_service.exception.AlreadyExistsException;
import com.internship.user_service.exception.UserNotFoundException;
import com.internship.user_service.mapper.UserMapper;
import com.internship.user_service.model.Availability;
import com.internship.user_service.model.User;
import com.internship.user_service.repository.AvailabilityRepository;
import com.internship.user_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

    @Mock
    private AvailabilityRepository availabilityRepository;

    @Mock
    private AvailabilityMapper availabilityMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDTO userDTO;
    private UserResponse userResponse;
    private AvailabilityDTO availabilityDTO;
    private WorkingHoursRequest workingHoursRequest;

    private Authentication authentication = mock(Authentication.class);
    private SecurityContext securityContext = mock(SecurityContext.class);

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

        availabilityDTO = new AvailabilityDTO();
        availabilityDTO.setWorkerId(1L);
        availabilityDTO.setStartTime(LocalDateTime.of(2025, 3, 25, 8, 0));
        availabilityDTO.setEndTime(LocalDateTime.of(2025, 3, 25, 12, 0));

        workingHoursRequest = WorkingHoursRequest
                .builder()
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(12, 0))
                .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn("1");
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void createUser() {
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
    void gettingUsersAvailabilitiesUserNotFound(){
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getAvailabilityForTheUser(anyLong()));
    }

    @Test
    void throwExceptionWhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.addAvailabilityToTheUser(availabilityDTO));
    }

    @Test
    void throwExceptionWhenStartTimeAfterEndTime() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        availabilityDTO.setStartTime(LocalDateTime.of(2025, 3, 25, 14, 0));
        availabilityDTO.setEndTime(LocalDateTime.of(2025, 3, 25, 12, 0));

        assertThrows(InvalidTimeFormatException.class, () -> userService.addAvailabilityToTheUser(availabilityDTO));
    }

    @Test
    void throwExceptionWhenUserIsBusy() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Availability existingAvailability = new Availability();
        existingAvailability.setStartTime(LocalDateTime.of(2025, 3, 25, 7, 0));
        existingAvailability.setEndTime(LocalDateTime.of(2025, 3, 25, 13, 0));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(availabilityRepository.findAllByUserId(1L)).thenReturn(List.of(existingAvailability));

        assertThrows(UserUnavailableException.class, () -> userService.addAvailabilityToTheUser(availabilityDTO));
    }

    @Test
    void shouldSaveAvailabilitySuccessfully() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(availabilityRepository.findAllByUserId(1L)).thenReturn(List.of());
        Availability availability = new Availability();
        when(availabilityMapper.toEntity(availabilityDTO)).thenReturn(availability);

        assertDoesNotThrow(() -> userService.addAvailabilityToTheUser(availabilityDTO));
        verify(availabilityRepository, times(1)).save(availability);
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

    @Test
    void updateUser_shouldUpdateUser_whenUserExists() {

        userDTO.setId(1L);
        userDTO.setName("Stefan");
        userDTO.setSurname("Stefanovic");
        userDTO.setBirthday(LocalDate.of(1990, 1, 1));
        userDTO.setAddress("Address1");
        userDTO.setPhone("123456789");
        userDTO.setCountry("Macedonia");
        userDTO.setCity("Warsaw");
        userDTO.setZipCode("12345");


        userResponse.setId(1L);
        userResponse.setName("Stefan");
        userResponse.setSurname("Stefanovic");
        userResponse.setAddress("Address1");
        userResponse.setPhone("123456789");
        userResponse.setCountry("Macedonia");
        userResponse.setCity("Warsaw");
        userResponse.setZipCode("12345");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.editUser(userDTO);

        assertNotNull(result);
        assertEquals(1, user.getId());
        assertEquals("Stefan", user.getName());
        assertEquals("Stefanovic", user.getSurname());
        assertEquals("Address1", user.getAddress());
        assertEquals("123456789", user.getPhone());
        assertEquals("Macedonia", user.getCountry());
        assertEquals("Warsaw", user.getCity());
        assertEquals("12345", user.getZipCode());

        verify(userRepository, times(1)).findById(1L);
        verify(userMapper, times(1)).toUserResponse(user);
    }

    @Test
    void updateUser_shouldThrowException_whenUserDoesNotExist() {

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.editUser(userDTO)
        );

        assertNotNull(exception);
        assertEquals("User not found.", exception.getMessage());
    }

    @Test
    void updateUser_shouldThrowIllegalArgumentException_whenUserDTOHasIdNull() {

        userDTO.setId(null);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.editUser(userDTO)
        );

        assertNotNull(exception);
        assertEquals("User ID cannot be null", exception.getMessage());
    }

    @Test
    void updateWorkingHours_shouldThrowException_whenUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.updateWorkingHours(workingHoursRequest)
        );

        assertNotNull(exception);
        assertEquals("User with id 1 was not found.", exception.getMessage());
        verify(userRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateWorkingHours_shouldThrowConflictExceptionIfStartTimeIsAfterEndTime() {
        workingHoursRequest.setEndTime(LocalTime.of(7, 0));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> userService.updateWorkingHours(workingHoursRequest)
        );

        assertNotNull(exception);
        assertEquals("Start time must be before end time.", exception.getMessage());
        verify(userRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateWorkingHours_shouldThrowConflictException() {
        workingHoursRequest.setEndTime(LocalTime.of(8, 25));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> userService.updateWorkingHours(workingHoursRequest)
        );

        assertNotNull(exception);
        assertEquals("Your working hours must be at least 30 minutes.", exception.getMessage());
        verify(userRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateWorkingHours_shouldBeSuccessful() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.updateWorkingHours(workingHoursRequest);

        assertEquals(workingHoursRequest.getStartTime(), user.getStartTime());
        assertEquals(workingHoursRequest.getEndTime(), user.getEndTime());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(user);
    }
}