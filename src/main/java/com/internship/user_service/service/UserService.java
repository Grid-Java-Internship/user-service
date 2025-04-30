package com.internship.user_service.service;

import com.internship.user_service.dto.AvailabilityDTO;
import com.internship.user_service.dto.UserDTO;
import com.internship.user_service.dto.UserResponse;
import com.internship.user_service.exception.PictureNotFoundException;
import com.internship.user_service.exception.UserNotFoundException;
import com.internship.user_service.model.Availability;
import com.internship.user_service.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {

    /**
     * Creates a new user based on the given {@link UserDTO}.
     *
     * @param userDTO The user to be created.
     * @return The created user.
     */
    UserResponse createUser(UserDTO userDTO);

    /**
     * Adds a profile picture for the user with the specified ID.
     *
     * @param id The ID of the user for whom the profile picture is to be added.
     * @param file The MultipartFile containing the profile picture to be uploaded.
     * @return A UserResponse containing the updated user information with the new profile picture path.
     * @throws UserNotFoundException if the user with the specified ID does not exist.
     * @throws PictureNotFoundException if the file is empty or not a valid image type.
     */
    UserResponse addProfilePicture(Long id, MultipartFile file) throws IOException;

    /**
     * Deletes the profile picture associated with the user with the given ID.
     * <p>
     * If the user with the given ID does not exist, a {@link UserNotFoundException} is thrown.
     * <p>
     * The method will return true if the profile picture was successfully deleted, or false if the user with the given id does not exist.
     * @param id The ID of the user for whom the profile picture is to be deleted.
     * @return {@code true} if the profile picture was successfully deleted, or {@code false} if the user with the given id does not exist.
     * @throws UserNotFoundException if the user with the specified ID does not exist.
     */
    Boolean deleteProfilePicture(Long id);

    /**
     * Deletes the profile picture with the specified name.
     *
     * @param user User whose picture we are deleting
     * @return {@code true} if the profile picture was successfully deleted, or {@code false} if the picture does not exist.
     * @throws IllegalArgumentException if the pictureName is null or empty.
     */
    Boolean deleteProfilePicture(User user);

    /**
     * Returns the user with the specified ID.
     *
     * @param id The ID of the user to be retrieved.
     * @return The user with the specified ID.
     * @throws UserNotFoundException if the user with the specified ID does not exist.
     */
    UserResponse getUser(Long id);

    /**
     * Retrieves all users from the repository.
     *
     * @return A list of UserResponse objects representing all users.
     */
    List<UserResponse> getAllUsers();

    List<Availability> getAvailabilityForTheUser(Long userId);

    void addAvailabilityToTheUser(AvailabilityDTO availabilityDTO);

    /**
     * Undo the creation of the user with the given ID.
     *
     * @param id The ID of the user to be deleted.
     * @return {@code true} if the user has been deleted, or {@code false} if the user
     * with the given ID does not exist.
     */
    Boolean undoUserCreation(Long id);

    UserResponse editUser(UserDTO userDTO);

    /**
     * Fetches the user from the repository by its ID.
     * <p>
     * This method is used for internal service use only and should not be used from the outside.
     *
     * @param userId The ID of the user to be fetched.
     * @return The user with the specified ID.
     * @throws UserNotFoundException if the user with the specified ID does not exist.
     */
    User getUserEntity(Long userId);

    boolean checkIfPhoneExists(String phoneNumber);
}
