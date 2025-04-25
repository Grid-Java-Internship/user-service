package com.internship.user_service.service;

import com.internship.user_service.dto.PreferencesDTO;
import com.internship.user_service.exception.ConflictException;
import com.internship.user_service.exception.UserNotFoundException;
import com.internship.user_service.model.Preferences;
import com.internship.user_service.model.User;
import com.internship.user_service.model.WantedCategory;
import com.internship.user_service.model.WantedCategoryId;
import com.internship.user_service.repository.PreferencesRepository;
import com.internship.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PreferenceServiceImpl implements PreferenceService {

    private final UserRepository userRepository;
    private final PreferencesRepository preferencesRepository;

    @Override
    @Transactional
    public PreferencesDTO setPreferences(PreferencesDTO preferenceDTO) {
        User user = userRepository.findById(Long.parseLong((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal()))
                .orElseThrow(() -> new UserNotFoundException("User not found."));


        preferencesRepository.findById(user.getId())
                .ifPresent(p -> { throw new ConflictException("Preferences already set."); });

        Preferences preferences = Preferences.builder()
                .user(user)
                .preferredDistance(preferenceDTO.getPreferredDistance())
                .preferredExperience(preferenceDTO.getPreferredExperience())
                .wantedCategories(preferenceDTO.getWantedCategories().stream()
                        .map(category -> WantedCategory.builder()
                                .preferences(null)
                                .wantedCategoryId(WantedCategoryId.builder()
                                        .categoryId(category)
                                        .build())
                                .build())
                        .toList())
                .build();

        preferences.getWantedCategories().forEach(wc -> wc.setPreferences(preferences));

        preferencesRepository.save(preferences);

        return preferenceDTO;
    }

    @Override
    @Transactional
    public PreferencesDTO updatePreferences(PreferencesDTO preferenceDTO) {
        Preferences preferences = preferencesRepository.findById(Long.parseLong((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal()))
                .orElseThrow(() -> new UserNotFoundException("User not found."));

        preferences.setPreferredDistance(preferenceDTO.getPreferredDistance());
        preferences.setPreferredExperience(preferenceDTO.getPreferredExperience());

        preferences.getWantedCategories().clear();

        preferenceDTO.getWantedCategories().forEach(category -> {
            WantedCategory wantedCategory = WantedCategory.builder()
                    .preferences(preferences)
                    .wantedCategoryId(WantedCategoryId.builder()
                            .categoryId(category)
                            .preferencesId(preferences.getId())
                            .build())
                    .build();
            preferences.getWantedCategories().add(wantedCategory);
        });

        preferencesRepository.save(preferences);

        return preferenceDTO;
    }
}
