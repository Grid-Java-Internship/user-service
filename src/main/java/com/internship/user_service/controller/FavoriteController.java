package com.internship.user_service.controller;

import com.internship.user_service.dto.FavoriteResponse;
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
     * Returns a list of IDs of users who are favorites of the user with the given
     * {@code userId}.
     *
     * @param page       The page number.
     * @param pageSize   The page size.
     * @return A list of user IDs.
     */
    @GetMapping
    public ResponseEntity<List<Long>> getFavoriteUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        return ResponseEntity.ok(favoriteService.getFavoriteUsers(page, pageSize));
    }

    /**
     * Adds a favorite user to the user with the given {@code userId}.
     *
     * @param favoriteUserId The id of the user to add as a favorite
     * @return {@code true} if the user was added as a favorite, {@code false} otherwise
     */
    @PostMapping("/{favoriteUserId}")
    public ResponseEntity<FavoriteResponse> addFavorite(@PathVariable Long favoriteUserId) {
        return ResponseEntity.ok(favoriteService.addFavorite(favoriteUserId));
    }

    /**
     * Deletes a favorite user from the user with the given {@code userId}.
     *
     * @param userId         The id of the user
     * @param favoriteUserId The id of the user to delete as a favorite
     * @return A 204 NO CONTENT response if the favorite was deleted, a 400 BAD REQUEST with an
     * error message if the favorite relationship does not exist
     */
    @DeleteMapping("/{favoriteUserId}")
    public ResponseEntity<Void> deleteFavorite(@PathVariable Long favoriteUserId) {
        favoriteService.deleteFavorite(favoriteUserId);
        return ResponseEntity.noContent().build();
    }
}
