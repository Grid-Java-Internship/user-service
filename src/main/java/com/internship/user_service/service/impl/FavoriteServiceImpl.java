package com.internship.user_service.service.impl;

import com.internship.user_service.dto.FavoriteResponse;
import com.internship.user_service.dto.UserResponse;
import com.internship.user_service.exception.AlreadyExistsException;
import com.internship.user_service.mapper.FavoriteMapper;
import com.internship.user_service.mapper.UserMapper;
import com.internship.user_service.model.Favorite;
import com.internship.user_service.model.FavoriteId;
import com.internship.user_service.model.User;
import com.internship.user_service.repository.FavoriteRepository;
import com.internship.user_service.service.BlockService;
import com.internship.user_service.service.FavoriteService;
import com.internship.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final FavoriteMapper favoriteMapper;

    private final UserService userService;
    private final BlockService blockService;

    @Override
    public List<Long> getFavoriteUsers(Long userId, int page, int pageSize) {
        // Retrieve the user if it exists
        User user = userService.getUserEntity(userId);

        // Retrieve favorite users
        List<Long> favoriteUsers = getFavoriteUsersPage(user, page, pageSize).getContent();

        // If there are no favorite users, return empty list
        if (favoriteUsers.isEmpty()) {
            log.info("User with userId {} has no favorite users.", userId);
        } else {
            log.info("Retrieved favorite users for user with userId {}.", userId);
        }

        // Return favorite users
        return favoriteUsers;
    }

    @Override
    public FavoriteResponse addFavorite(Long userId, Long favoriteUserId) {
        // Check if userId and favoriteUserId are valid
        verifyFavoriteUserIds(userId, favoriteUserId);

        // Retrieve users if they exist
        User user = userService.getUserEntity(userId);
        User favoriteUser = userService.getUserEntity(favoriteUserId);

        // Check if user blocked favoriteUser
        if (blockService.blockExists(userId, favoriteUserId)) {
            log.error("User with userId {} has already blocked user with userId {}.", userId, favoriteUserId);
            throw new IllegalArgumentException("User blocked user to be favorited.");
        }

        // Check if favorite already exists
        if (favoriteExists(userId, favoriteUserId)) {
            log.error("Favorite with userId {} and favoriteUserId {} already exists.", userId, favoriteUserId);
            throw new AlreadyExistsException("Favorite relationship already exists.");
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
        userService.getUserEntity(userId);
        userService.getUserEntity(favoriteUserId);

        // Check if favorite exists
        if (!favoriteExists(userId, favoriteUserId)) {
            log.error("Favorite with userId {} and favoriteUserId {} does not exist.", userId, favoriteUserId);
            throw new IllegalArgumentException("Favorite relationship does not exist.");
        }

        // Delete favorite user
        favoriteRepository.deleteById(new FavoriteId(userId, favoriteUserId));
        log.info("Favorite with userId {} and favoriteUserId {} deleted successfully.", userId, favoriteUserId);
    }

    @Override
    public boolean favoriteExists(Long userId, Long favoriteUserId) {
        FavoriteId favoriteId = new FavoriteId(userId, favoriteUserId);
        return favoriteRepository.existsById(favoriteId);
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
     * Retrieves a page of users who are favorites of the given {@code user}.
     * <p>
     * The page size is determined by the given {@code pageSize}, and the page number
     * is determined by the given {@code page}.
     * <p>
     * The returned page contains the favorite users mapped from the page of
     * {@link Favorite} objects returned by the repository.
     *
     * @param user     The user whose favorite users are to be retrieved.
     * @param page     The page number.
     * @param pageSize The page size.
     * @return A page of favorite users.
     */
    private Page<Long> getFavoriteUsersPage(User user, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return favoriteRepository
                .findByUser(user, pageable)
                .map(Favorite::getId)
                .map(FavoriteId::getFavoriteUserId);
    }
}