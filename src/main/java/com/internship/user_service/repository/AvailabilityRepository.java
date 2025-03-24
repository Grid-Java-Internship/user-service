package com.internship.user_service.repository;

import com.internship.user_service.model.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
    List<Availability> findAllByUserId(Long userId);

    List<Availability> findAllByUserIdAndStartTime(Long userId, LocalDateTime dateOfReservation);
}
