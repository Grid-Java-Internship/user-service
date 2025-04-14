package com.internship.user_service.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
