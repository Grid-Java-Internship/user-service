package com.internship.user_service.controller;

import com.internship.user_service.dto.FavoriteResponse;
import com.internship.user_service.dto.UserResponse;
import com.internship.user_service.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/favorites")
@RequiredArgsConstructor
public class FavoriteController {
    private final FavoriteService favoriteService;

    /**
     * Retrieves the list of favorite users for the given {@code userId}.
     *
     * @param userId The ID of the user whose favorite users are to be retrieved.
     * @return A {@link ResponseEntity} containing a list of {@link UserResponse} objects representing the favorite users.
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>> getFavoriteUsers(@RequestParam Long userId) {
        return ResponseEntity.ok(favoriteService.getFavoriteUsers(userId));
    }

    /**
     * Adds a favorite user to the user with the given {@code userId}.
     *
     * @param userId         The id of the user
     * @param favoriteUserId The id of the user to add as a favorite
     * @return {@code true} if the user was added as a favorite, {@code false} otherwise
     */
    @PostMapping("/{userId}/{favoriteUserId}")
    public ResponseEntity<FavoriteResponse> addFavorite(@PathVariable Long userId,
                                                        @PathVariable Long favoriteUserId) {
        return ResponseEntity.ok(favoriteService.addFavorite(userId, favoriteUserId));
    }

    /**
     * Deletes a favorite user from the user with the given {@code userId}.
     *
     * @param userId         The id of the user
     * @param favoriteUserId The id of the user to delete as a favorite
     * @return A 204 NO CONTENT response if the favorite was deleted, a 400 BAD REQUEST with an
     * error message if the favorite relationship does not exist
     */
    @DeleteMapping("/{userId}/{favoriteUserId}")
    public ResponseEntity<Void> deleteFavorite(@PathVariable Long userId,
                                               @PathVariable Long favoriteUserId) {
        favoriteService.deleteFavorite(userId, favoriteUserId);
        return ResponseEntity.noContent().build();
    }
}
