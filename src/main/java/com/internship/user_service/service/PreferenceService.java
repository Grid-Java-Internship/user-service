package com.internship.user_service.service;


import com.internship.user_service.dto.PreferencesDTO;
import com.internship.user_service.exception.ConflictException;
import com.internship.user_service.exception.UserNotFoundException;

public interface PreferenceService {

    /**
     * Sets the user preferences based on the given {@link PreferencesDTO}.
     * <p>
     * If the user does not exist, a {@link UserNotFoundException} is thrown.
     * <p>
     * If preferences for the user already exist, a {@link ConflictException} is thrown.
     * <p>
     * The method will return the created {@link PreferencesDTO}.
     * @param preferenceDTO The preferences to be created.
     * @return The created preferences.
     */
    PreferencesDTO setPreferences(PreferencesDTO preferenceDTO);

    /**
     * Updates the user preferences based on the given {@link PreferencesDTO}.
     * <p>
     * If the user does not exist, a {@link UserNotFoundException} is thrown.
     * <p>
     * The method will update the given preferences and also update the wanted categories.
     * If a wanted category does not exist, it will be added. If a wanted category already
     * exists, it will be updated. If a wanted category is deleted, it will be removed.
     * <p>
     * The method will return the updated {@link PreferencesDTO}.
     * @param preferenceDTO The preferences to be updated.
     * @return The updated preferences.
     */
    PreferencesDTO updatePreferences(PreferencesDTO preferenceDTO);

    /**
     * Returns the user preferences for the given user id.
     * <p>
     * If the user does not exist, a {@link UserNotFoundException} is thrown.
     * <p>
     * The method will return the {@link PreferencesDTO} for the given user.
     * @param userId The id of the user.
     * @return The user preferences.
     * @throws UserNotFoundException If the user does not exist.
     */
    PreferencesDTO getPreferences(Long userId);
}
