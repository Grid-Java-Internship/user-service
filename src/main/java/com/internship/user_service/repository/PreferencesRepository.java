package com.internship.user_service.repository;

import com.internship.user_service.model.Preferences;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PreferencesRepository extends JpaRepository<Preferences, Long> {
}
