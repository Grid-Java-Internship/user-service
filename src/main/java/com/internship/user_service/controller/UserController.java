package com.internship.user_service.controller;

import com.internship.user_service.dto.AvailabilityDTO;
import com.internship.user_service.dto.ImageDTO;
import com.internship.user_service.dto.UserDTO;
import com.internship.user_service.dto.UserResponse;
import com.internship.user_service.dto.WorkingHoursRequest;
import com.internship.user_service.model.Availability;
import com.internship.user_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/users")
public class UserController {

    private final UserService userService;

    /**
     * Creates a new user based on the given {@link UserDTO}.
     * @param userDTO The user to create
     * @return The created user
     */
    @PostMapping("/createUser")
    public ResponseEntity<UserResponse> createUser(@RequestBody @Valid UserDTO userDTO) {
        UserResponse userResponse = userService.createUser(userDTO);
        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
    }

    /**
     * Updates the user with given {@code id} by adding the given {@link MultipartFile} as a profile picture.
     * @param id The id of the user
     * @param file The file to add as a profile picture
     * @return The updated user
     */
    @PatchMapping("/{id}/addProfilePicture")
    public ResponseEntity<UserResponse> addProfilePicture(@PathVariable Long id, @RequestPart("file") MultipartFile file) throws IOException {
        UserResponse userResponse = userService.addProfilePicture(id, file);
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    /**
     * Deletes the profile picture of the user with the given {@code id}.
     * @param id The id of the user
     * @return {@code true} if the profile picture was deleted, {@code false} if the user with the given id does not exist
     */
    @DeleteMapping("/{id}/deleteProfilePicture")
    public ResponseEntity<Boolean> deleteProfilePicture(@PathVariable Long id) {
        Boolean deleted = userService.deleteProfilePicture(id);
        return ResponseEntity.ok(deleted);
    }

    /**
     * Returns the profile picture of the user with the given {@code id}.
     * @param id The id of the user
     * @return The profile picture
     */
    @GetMapping("/{id}/getProfilePicture")
    public ResponseEntity<byte[]> getProfilePicture(@PathVariable Long id) {
        ImageDTO imageDTO = userService.getProfilePicture(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(imageDTO.mediaType);
        headers.setContentLength(imageDTO.image.length);

        return new ResponseEntity<>(imageDTO.image, headers, HttpStatus.OK);
    }

    /**
     * Returns the user with given {@code id}.
     * @param id The id of the user
     * @return The user
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        UserResponse userResponse = userService.getUser(id);
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    /**
     * Returns all users.
     * @return A list of all users
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    /**
     * Returns all availabilities for the user with given {@code id}.
     * @param userId The id of the user
     * @return A list of all availabilities for the user with given {@code id}
     */
    @GetMapping("/available/{id}")
    public ResponseEntity<List<Availability>> getAvailabilityForTheUser(@PathVariable("id") Long userId){
        return ResponseEntity.ok().body(userService.getAvailabilityForTheUser(userId));
    }

    /**
     * Adds availability for the user based on the given {@link AvailabilityDTO}.
     *
     * @param availabilityDTO The availability data to be added for the user.
     * @return A ResponseEntity with HTTP status OK if the availability is added successfully.
     */
    @PostMapping("/available")
    public ResponseEntity<Void> addAvailability(@RequestBody @Valid AvailabilityDTO availabilityDTO){
        userService.addAvailabilityToTheUser(availabilityDTO);

        return ResponseEntity.ok().build();
    }

    /**
     * Deletes the user with the given {@code id}.
     * @param id The id of the user
     * @return {@code true} if the user was deleted, {@code false} if the user was not found
     */
    @DeleteMapping("/deleteUser/{id}")
    public ResponseEntity<Boolean> deleteUser(@PathVariable Long id) {
        Boolean isDeleted = userService.undoUserCreation(id);
        return new ResponseEntity<>(isDeleted, HttpStatus.OK);
    }

    @PatchMapping("/editUser")
    public ResponseEntity<UserResponse> editUser(@RequestBody @Valid UserDTO userDTO) {
        UserResponse userResponse = userService.editUser(userDTO);
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    @GetMapping("/exists/by-phone")
    public ResponseEntity<Boolean> checkUserExistsByPhone(@RequestParam("phone") String phoneNumber) {
        boolean exists = userService.checkIfPhoneExists(phoneNumber);
        return ResponseEntity.ok(exists);
    }

    @PatchMapping("/workingHours")
    public ResponseEntity<Void> updateWorkingHours(@RequestBody @Valid WorkingHoursRequest request) {
        userService.updateWorkingHours(request);
        return ResponseEntity.noContent().build();
    }

}
