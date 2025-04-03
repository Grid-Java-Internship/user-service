package com.internship.user_service.service.impl;

import com.internship.user_service.exception.AlreadyExistsException;
import com.internship.user_service.model.Block;
import com.internship.user_service.model.BlockId;
import com.internship.user_service.model.User;
import com.internship.user_service.repository.BlockRepository;
import com.internship.user_service.service.BlockService;
import com.internship.user_service.service.FavoriteService;
import com.internship.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlockServiceImpl implements BlockService {
    private final BlockRepository blockRepository;
    private final UserService userService;

    @Lazy
    private final FavoriteService favoriteService;

    @Transactional
    @Override
    public void blockUser(Long userId, Long blockedUserId) {
        // Check if userId and blockedUserId are valid
        verifyBlockUserIds(userId, blockedUserId);

        // Retrieve users if they exist
        User user = userService.getUserEntity(userId);
        User blockedUser = userService.getUserEntity(blockedUserId);

        // Check if block already exists
        if (blockExists(userId, blockedUserId)) {
            log.error("Block with userId {} and blockedUserId {} already exists.", userId, blockedUserId);
            throw new AlreadyExistsException("Block relationship already exists.");
        }

        // Add block for the user
        Block block = blockRepository.save(new Block(
                new BlockId(userId, blockedUserId),
                user,
                blockedUser));
        log.info("Block with userId {} and blockedUserId {} added successfully.",
                block.getId().getBlockingUserId(),
                block.getId().getBlockedUserId());

        // Remove blockedUser from favorites if it was favorited by user
        if (favoriteService.favoriteExists(userId, blockedUserId)) {
            favoriteService.deleteFavorite(userId, blockedUserId);
        }
    }

    @Override
    public void unblockUser(Long userId, Long blockedUserId) {
        // Check if userId and blockedUserId are valid
        verifyBlockUserIds(userId, blockedUserId);

        // Check if users exist
        userService.getUserEntity(userId);
        userService.getUserEntity(blockedUserId);

        // Check if block exists
        if (!blockExists(userId, blockedUserId)) {
            log.error("Block with userId {} and blockedUserId {} does not exist.", userId, blockedUserId);
            throw new IllegalArgumentException("Block relationship does not exist.");
        }

        // Delete block user
        blockRepository.deleteById(new BlockId(userId, blockedUserId));
        log.info("Block with userId {} and blockedUserId {} deleted successfully.", userId, blockedUserId);
    }

    public boolean blockExists(Long userId, Long blockedUserId) {
        BlockId blockId = new BlockId(userId, blockedUserId);
        return blockRepository.existsById(blockId);
    }

    /**
     * Verifies that the given user IDs are valid.
     * <p>
     * A valid user ID is a non-null and non-zero value.
     * <p>
     * If the given IDs are invalid, an {@link IllegalArgumentException} is thrown.
     *
     * @param userId        The ID of the user who is blocking another user.
     * @param blockedUserId The ID of the user who is being blocked.
     */
    private void verifyBlockUserIds(Long userId, Long blockedUserId) {
        if (userId == null || blockedUserId == null || userId.equals(blockedUserId)) {
            log.error("Invalid userId {} or blockedUserId {}.", userId, blockedUserId);
            throw new IllegalArgumentException("Invalid userId or blockedUserId.");
        }
    }
}
