package com.internship.user_service.model;

import com.internship.user_service.model.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private String name;

    @NotNull
    @Column(nullable = false)
    private String lastname;

    @NotNull
    @Email
    @Column(unique = true, nullable = false)
    private String email;

    @Past
    private LocalDate birthday;

    private String phone;

    private String address;

    @CreationTimestamp
    private LocalDate created;

    private Boolean verified;

    private String profilePicturePath;

    @NotNull
    @Column(nullable = false)
    private Status status;

    @OneToMany(mappedBy = "user")
    List<Availability> availabilities;

}