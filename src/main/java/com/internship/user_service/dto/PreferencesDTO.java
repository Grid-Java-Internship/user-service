package com.internship.user_service.dto;

import com.internship.user_service.enums.JobCategory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreferencesDTO {

    private Long userId;

    @NotNull
    @Min(0)
    private Double preferredDistance;

    @NotNull
    @Min(0)
    private Integer preferredExperience;

    @NotNull
    private List<JobCategory> wantedCategories;
}
