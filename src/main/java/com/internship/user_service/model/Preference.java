package com.internship.user_service.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
@Entity
@Table(name = "preferences")
public class Preference {

    @Id
    @JoinColumn
    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    private Double preferredDistance;

    private Integer preferredExperience;

    @OneToMany(mappedBy = "preference")
    private List<WantedCategory> wantedCategories;
}
