package com.internship.user_service.controller;

import com.internship.user_service.dto.PreferencesDTO;
import com.internship.user_service.service.PreferenceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/preferences")
public class PreferencesController {

    private final PreferenceService preferenceService;

    /**
     * Creates a new user preferences based on the given {@link PreferencesDTO}.
     *
     * @param preferenceDTO The preferences to be created.
     * @return The created preferences.
     */
    @PostMapping
    public ResponseEntity<PreferencesDTO> setPreferences(@RequestBody @Valid PreferencesDTO preferenceDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(preferenceService.setPreferences(preferenceDTO));
    }

    /**
     * Updates the user preferences based on the given {@link PreferencesDTO}.
     *
     * @param preferenceDTO The preferences to be updated.
     * @return The updated preferences.
     */
    @PutMapping
    public ResponseEntity<PreferencesDTO> updatePreferences(@RequestBody @Valid PreferencesDTO preferenceDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(preferenceService.updatePreferences(preferenceDTO));
    }

    /**
     * Returns the preferences of the user with the given {@code userId}.
     *
     * @param userId The ID of the user whose preferences are to be retrieved.
     * @return The preferences of the given user.
     */
    @GetMapping("/{userId}")
    public ResponseEntity<PreferencesDTO> getPreferences(@PathVariable Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(preferenceService.getPreferences(userId));
    }
}
