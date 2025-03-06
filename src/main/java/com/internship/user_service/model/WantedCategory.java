package com.internship.user_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "wanted_categories")
public class WantedCategory {

    @EmbeddedId
    private WantedCategoryId wantedCategoryId;

    @ManyToOne
    @MapsId("preferenceId")
    @JoinColumn(name = "preference_id")
    private Preference preference;

}
