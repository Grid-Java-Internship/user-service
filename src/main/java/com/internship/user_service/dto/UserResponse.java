package com.internship.user_service.dto;


import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private Long id;

    @NotBlank(message = "Name is mandatory.")
    @Size(min = 2, max = 30, message = "Name length must be between {min} and {max} characters.")
    private String name;

    @NotBlank(message = "Surname is mandatory.")
    @Size(min = 2, max = 30, message = "Surname length must be between {min} and {max} characters.")
    private String surname;

    @NotBlank(message = "Email is mandatory.")
    @Size(min = 2, max = 30, message = "Email length must be between {min} and {max} characters.")
    @Email(message = "Email is mandatory.")
    private String email;

    @NotEmpty(message = "Phone number is mandatory.")
    @Pattern(regexp = "^(\\+\\d{1,2}\\s?)?1?-?\\.?\\s?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4}$",
            message = "Phone number is mandatory.")
    private String phone;

    @NotBlank(message = "Address is mandatory.")
    @Size(min = 2, max = 30, message = "Address length must be between {min} and {max} characters.")
    private String address;

    private String profilePicturePath;

}
