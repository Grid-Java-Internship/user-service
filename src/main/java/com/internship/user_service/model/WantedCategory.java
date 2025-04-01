package com.internship.user_service.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "wanted_categories")
public class WantedCategory {

    @EmbeddedId
    private WantedCategoryId wantedCategoryId;

    @ManyToOne
    @MapsId("preferencesId")
    @JoinColumn(name = "preferences_id", insertable = false, updatable = false)
    private Preferences preferences;

}
