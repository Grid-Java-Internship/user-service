package com.internship.user_service.model;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@Getter
@EqualsAndHashCode
@Embeddable
public class FavoriteId implements Serializable {

    private Long userId;
    private Long favoriteUserId;
}
