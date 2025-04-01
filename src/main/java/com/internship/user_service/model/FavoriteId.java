package com.internship.user_service.model;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class FavoriteId implements Serializable {

    private Long userId;
    private Long favoriteUserId;
}
