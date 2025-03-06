package com.internship.user_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "blocks")
public class Block {

    @EmbeddedId
    private BlockId id;

    @ManyToOne
    @MapsId("blockingUserId")
    @JoinColumn(name = "blocking_user_id")
    private User blockingUser;

    @ManyToOne
    @MapsId("blockedUserId")
    @JoinColumn(name = "blocked_user_id")
    private User blockedUser;
}
