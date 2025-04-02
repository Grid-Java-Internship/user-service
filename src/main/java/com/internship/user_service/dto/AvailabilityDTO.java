package com.internship.user_service.dto;


import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AvailabilityDTO {
    @NotNull(message = "Enter a valid start time!")
    @Future(message = "Start time must be in the future!")
    private LocalDateTime startTime;

    @NotNull(message = "Enter a valid end time!")
    @Future(message = "End time must be in the future!")
    private LocalDateTime endTime;

    @NotNull(message = "Enter a valid worker id!")
    private Long workerId;
}
