package com.internship.user_service.service;

import com.internship.user_service.dto.AvailabilityDTO;
import com.internship.user_service.dto.ImageDTO;
import com.internship.user_service.dto.UserDTO;
import com.internship.user_service.dto.UserResponse;
import com.internship.user_service.exception.ServiceUnavailableException;
import com.internship.user_service.dto.WorkingHoursRequest;
import com.internship.user_service.exception.PictureNotFoundException;
import com.internship.user_service.exception.UserUnavailableException;
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
     * Retrieves the profile picture of the user with the specified ID.
     * <p>
     * If the user does not have a profile picture, an empty image will be returned.
     * </p>
     *
     * @param userId The ID of the user whose profile picture is to be retrieved.
     * @return An ImageDTO containing the profile picture of the user.
     * @throws UserNotFoundException if the user with the specified ID does not exist.
     * @throws ServiceUnavailableException if there is an error accessing the storage service.
     */
    ImageDTO getProfilePicture(Long userId);

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

    /**
     * Retrieves all availabilities for the user with the specified ID.
     * <p>
     * If the list of availabilities is empty, that means that the user is available
     * for the proposed time.
     * </p>
     *
     * @param userId The ID of the user for whom to retrieve the availabilities.
     * @return A list of all availabilities for the user with the specified {@code userId}.
     */
    List<Availability> getAvailabilityForTheUser(Long userId);

    /**
     * Adds availability for the user with the specified {@code userId} based on the
     * given {@link AvailabilityDTO}.
     *
     * @param availabilityDTO The availability data to be added for the user.
     * @throws UserNotFoundException if the user with the given ID does not exist.
     * @throws UserUnavailableException if the user is already busy in that time.
     */
    void addAvailabilityToTheUser(AvailabilityDTO availabilityDTO);

    /**
     * Undo the creation of the user with the given ID.
     *
     * @param id The ID of the user to be deleted.
     * @return {@code true} if the user has been deleted, or {@code false} if the user
     * with the given ID does not exist.
     */
    Boolean undoUserCreation(Long id);

    /**
     * Edits the user with the given ID, using the provided UserDTO object.
     * <p>
     * If the user with the given ID does not exist, a UserNotFoundException is thrown.
     * </p>
     *
     * @param userDTO The user data to be used for the edit operation.
     * @return A UserResponse containing the updated user data.
     * @throws UserNotFoundException if the user with the specified ID does not exist.
     */
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

    /**
     * Checks if a user with the given phone number exists in the repository.
     * <p>
     * This method is used for internal service use only and should not be used from the outside.
     *
     * @param phoneNumber The phone number to be checked.
     * @return true if a user with the given phone number exists, false otherwise.
     */
    boolean checkIfPhoneExists(String phoneNumber);

    void updateWorkingHours(WorkingHoursRequest request);
}
