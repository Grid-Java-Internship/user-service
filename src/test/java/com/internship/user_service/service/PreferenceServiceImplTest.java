package com.internship.user_service.service;

import com.internship.user_service.dto.PreferencesDTO;
import com.internship.user_service.enums.JobCategory;
import com.internship.user_service.exception.ConflictException;
import com.internship.user_service.exception.UserNotFoundException;
import com.internship.user_service.model.Preferences;
import com.internship.user_service.model.User;
import com.internship.user_service.model.WantedCategory;
import com.internship.user_service.model.WantedCategoryId;
import com.internship.user_service.repository.PreferencesRepository;
import com.internship.user_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PreferenceServiceImplTest {

    @Mock
    private PreferencesRepository preferencesRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PreferenceServiceImpl preferenceService;

    private User user;
    private Preferences preferences;
    private PreferencesDTO preferencesDTO;

    @BeforeEach
    void beforeEach() {
        user = User.builder()
                .id(1L)
                .email("email@email.com")
                .build();

        preferences = Preferences.builder()
                .user(user)
                .preferredExperience(2)
                .preferredDistance(3.0)
                .wantedCategories(new ArrayList<>(
                        List.of(
                                WantedCategory.builder()
                                        .preferences(preferences)
                                        .wantedCategoryId(new WantedCategoryId(JobCategory.ALL, user.getId()))
                                        .build()
                        ))
                )
                .build();

        preferencesDTO = PreferencesDTO.builder()
                .userId(1L)
                .preferredExperience(2)
                .preferredDistance(3.0)
                .wantedCategories(List.of(JobCategory.ALL))
                .build();
    }

    @Test
    void setPreferences_shouldSetPreferences_whenUserExistsAndHasNoPreferences() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(preferencesRepository.findById(anyLong())).thenReturn(Optional.empty());

        when(preferencesRepository.save(any(Preferences.class))).thenReturn(preferences);

        PreferencesDTO result = preferenceService.setPreferences(preferencesDTO);

        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals(2, result.getPreferredExperience());
        assertEquals(3.0, result.getPreferredDistance());
        assertEquals(List.of(JobCategory.ALL), result.getWantedCategories());
    }

    @Test
    void setPreferences_shouldThrowConflictException_whenUserExistsAndHasPreferences() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(preferencesRepository.findById(anyLong())).thenReturn(Optional.of(preferences));

        ConflictException exception = assertThrows(ConflictException.class, () -> preferenceService.setPreferences(preferencesDTO));

        assertNotNull(exception);
        assertEquals("Preferences already set.", exception.getMessage());
    }

    @Test
    void setPreferences_shouldThrowUserNotFoundException_whenUserDoesNotExist() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> preferenceService.setPreferences(preferencesDTO));

        assertNotNull(exception);
        assertEquals("User not found.", exception.getMessage());
    }

    @Test
    void updatePreferences_shouldUpdatePreferences_whenTheyExist() {
        when(preferencesRepository.findById(anyLong()))
                .thenReturn(Optional.of(preferences));
        when(preferencesRepository.save(any(Preferences.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PreferencesDTO result = preferenceService.updatePreferences(preferencesDTO);

        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals(2, result.getPreferredExperience());
        assertEquals(3.0, result.getPreferredDistance());
        assertEquals(List.of(JobCategory.ALL), result.getWantedCategories());
    }

    @Test
    void updatePreferences_shouldThrowUserNotFoundException_whenUserDoesNotExist() {
        when(preferencesRepository.findById(anyLong())).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> preferenceService.updatePreferences(preferencesDTO));

        assertNotNull(exception);
        assertEquals("User not found.", exception.getMessage());
    }
}