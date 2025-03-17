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
public class BlockId implements Serializable {

    private Long blockingUserId;
    private Long blockedUserId;
}
