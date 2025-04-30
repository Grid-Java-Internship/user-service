package com.internship.user_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkingHoursRequest {

    @NotNull(message = "User id is mandatory.")
    private Long userId;

    @NotNull(message = "Start time is mandatory.")
    private LocalTime startTime;

    @NotNull(message = "End time is mandatory.")
    private LocalTime endTime;

}
