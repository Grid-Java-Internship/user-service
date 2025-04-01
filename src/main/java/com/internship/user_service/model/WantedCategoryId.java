package com.internship.user_service.model;

import com.internship.user_service.enums.JobCategory;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class WantedCategoryId implements Serializable {

    private JobCategory categoryId;
    private Long preferencesId;

}
