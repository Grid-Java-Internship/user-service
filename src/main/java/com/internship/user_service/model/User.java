package com.internship.user_service.model;

import com.internship.user_service.model.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    private Long id;

    @NotNull
    @Column(nullable = false)
    private String name;

    @NotNull
    @Column(nullable = false)
    private String surname;

    @NotNull
    @Email
    @Column(unique = true, nullable = false)
    private String email;

    @NotNull
    @Past
    @Column(nullable = false)
    private LocalDate birthday;

    @NotNull
    @Column(nullable = false)
    private String phone;

    @NotNull
    @Column(nullable = false)
    private String address;

    @NotNull
    @Column(nullable = false)
    private String city;

    @NotNull
    @Column(nullable = false)
    private String zipCode;

    @NotNull
    @Column(nullable = false)
    private String country;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime created;

    @NotNull
    @Column(nullable = false)
    private Boolean verified;

    private LocalTime startTime;

    private LocalTime endTime;

    private String profilePicturePath;

    @NotNull
    @Column(nullable = false)
    private Status status;

    @OneToMany(mappedBy = "user")
    List<Availability> availabilities;

}