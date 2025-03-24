package com.internship.user_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

// TODO Zauzetost se pamti u bazi
// TODO Znaci samo kada se izvrsi rezervacija onda se puni tabela availabilities

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "availabilities")
public class Availability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Future
    @Column(nullable = false)
    private LocalDateTime startTime;

    @NotNull
    @Future
    @Column(nullable = false)
    private LocalDateTime endTime;

    @JoinColumn
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

}
