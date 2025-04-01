package com.internship.user_service.repository;

import com.internship.user_service.model.WantedCategory;
import com.internship.user_service.model.WantedCategoryId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WantedCategoryRepository extends JpaRepository<WantedCategory, WantedCategoryId> {

}
