package com.internship.user_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    private String lastname;

    @Email
    private String email;

    @Past
    private LocalDate birthday;

    private String phone;

    private String address;

    @CreationTimestamp
    private LocalDate created;

    private String role;

    private Boolean verified;

    private String profilePicturePath;
}