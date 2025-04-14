package com.internship.user_service.service;

import com.internship.user_service.dto.AvailabilityDTO;
import com.internship.user_service.dto.UserDTO;
import com.internship.user_service.dto.UserResponse;
import com.internship.user_service.exception.PictureNotFoundException;
import com.internship.user_service.exception.UserNotFoundException;
import com.internship.user_service.model.Availability;
import com.internship.user_service.model.User;
import org.springframework.web.multipart.MultipartFile;

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
    UserResponse addProfilePicture(Long id, MultipartFile file);

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
    User getUserToService(Long userId);
}
