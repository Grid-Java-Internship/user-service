package com.internship.user_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "preferences")
public class Preferences {

    @Id
    private Long id;

    @MapsId
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id")
    private User user;

    private Double preferredDistance;

    private Integer preferredExperience;

    @OneToMany(mappedBy = "preferences", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WantedCategory> wantedCategories;
}
