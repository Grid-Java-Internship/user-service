package com.internship.user_service.service;

import com.internship.user_service.dto.FavoriteResponse;
import com.internship.user_service.exception.FavoriteAlreadyExistsException;
import com.internship.user_service.mapper.FavoriteMapper;
import com.internship.user_service.model.Favorite;
import com.internship.user_service.model.FavoriteId;
import com.internship.user_service.model.User;
import com.internship.user_service.repository.FavoriteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final UserService userService;
    private final FavoriteMapper favoriteMapper;

    @Override
    public FavoriteResponse addFavorite(Long userId, Long favoriteUserId) {
        // Check if userId and favoriteUserId are valid
        verifyFavoriteUserIds(userId, favoriteUserId);

        // Retrieve users if they exist
        User user = userService.getUserToService(userId);
        User favoriteUser = userService.getUserToService(favoriteUserId);

        // TODO: Check if user blocked the favoriteUser

        // Check if favorite already exists
        if (favoriteExists(userId, favoriteUserId)) {
            log.error("Favorite with userId {} and favoriteUserId {} already exists.", userId, favoriteUserId);
            throw new FavoriteAlreadyExistsException("Favorite relationship already exists.");
        }

        // Add favorite user
        Favorite favorite = favoriteRepository.save(new Favorite(
                new FavoriteId(userId, favoriteUserId),
                user,
                favoriteUser));
        log.info("Favorite with userId {} and favoriteUserId {} added successfully.",
                favorite.getId().getUserId(),
                favorite.getId().getFavoriteUserId());
        return favoriteMapper.toResponse(favorite);
    }

    @Override
    public void deleteFavorite(Long userId, Long favoriteUserId) {
        // Check if userId and favoriteUserId are valid
        verifyFavoriteUserIds(userId, favoriteUserId);

        // Retrieve users if they exist
        userService.getUserToService(userId);
        userService.getUserToService(favoriteUserId);

        // Check if favorite exists
        if (!favoriteExists(userId, favoriteUserId)) {
            log.error("Favorite with userId {} and favoriteUserId {} does not exist.", userId, favoriteUserId);
            throw new IllegalArgumentException("Favorite relationship does not exist.");
        }

        // Delete favorite user
        favoriteRepository.deleteById(new FavoriteId(userId, favoriteUserId));
        log.info("Favorite with userId {} and favoriteUserId {} deleted successfully.", userId, favoriteUserId);
    }

    /**
     * Verifies that the given user IDs are valid.
     * <p>
     * A valid user ID is a non-null and non-zero value.
     * <p>
     * If the given IDs are invalid, an {@link IllegalArgumentException} is thrown.
     *
     * @param userId         The ID of the user to whom the favorite is to be added.
     * @param favoriteUserId The ID of the user to be added as a favorite.
     */
    private void verifyFavoriteUserIds(Long userId, Long favoriteUserId) {
        if (userId == null || favoriteUserId == null || userId.equals(favoriteUserId)) {
            log.error("Invalid userId {} or favoriteUserId {}.", userId, favoriteUserId);
            throw new IllegalArgumentException("Invalid userId or favoriteUserId.");
        }
    }

    /**
     * Verifies if a favorite with the given IDs exists.
     *
     * @param userId         The ID of the user to whom the favorite is to be added.
     * @param favoriteUserId The ID of the user to be added as a favorite.
     * @return {@code true} if the favorite with the given IDs exists, or {@code false} if it does not.
     */
    private boolean favoriteExists(Long userId, Long favoriteUserId) {
        FavoriteId favoriteId = new FavoriteId(userId, favoriteUserId);
        return favoriteRepository.existsById(favoriteId);
    }
}
