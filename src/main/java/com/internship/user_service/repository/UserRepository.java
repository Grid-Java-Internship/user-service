package com.internship.user_service.repository;

import com.internship.user_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT COUNT(u.id) > 0 FROM User u WHERE u.phone = :phoneNumber")
    boolean checkIfPhoneExists(String phoneNumber);
}
