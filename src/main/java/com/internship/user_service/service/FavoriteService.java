package com.internship.user_service.service;

import com.internship.user_service.dto.FavoriteResponse;
import com.internship.user_service.dto.UserResponse;

import java.util.List;

public interface FavoriteService {
    /**
     * Adds a favorite user to the user with the given {@code userId}.
     *
     * @param userId         The ID of the user to whom the favorite is to be added.
     * @param favoriteUserId The ID of the user to be added as a favorite.
     * @return {@code true} if the favorite has been added, or {@code false} if the favorite already exists.
     */
    FavoriteResponse addFavorite(Long userId, Long favoriteUserId);

    /**
     * Deletes a favorite user to the user with the given {@code userId}.
     *
     * @param userId         The ID of the user to whom the favorite is to be deleted.
     * @param favoriteUserId The ID of the user to be deleted as a favorite.
     */
    void deleteFavorite(Long userId, Long favoriteUserId);

    /**
     * Checks if a favorite relationship exists between the given user IDs.
     *
     * @param userId         The ID of the user to whom the favorite is to be checked.
     * @param favoriteUserId The ID of the user to be checked as a favorite.
     * @return {@code true} if the favorite relationship exists, or {@code false} if it does not.
     */
    boolean favoriteExists(Long userId, Long favoriteUserId);

    /**
     * Retrieves the list of favorite users for the given {@code userId}.
     *
     * @param userId     The ID of the user whose favorite users are to be retrieved.
     * @param page The page number to retrieve.
     * @param pageSize   The page size to retrieve.
     * @return A list of {@link UserResponse} objects representing the favorite users.
     */
    List<UserResponse> getFavoriteUsers(Long userId, int page, int pageSize);
}
