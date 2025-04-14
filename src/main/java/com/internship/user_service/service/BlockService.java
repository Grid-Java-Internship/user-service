package com.internship.user_service.service;

public interface BlockService {
    /**
     * Adds a block relationship between the user with the given {@code userId} and
     * the user with the given {@code blockedUserId}. This means that the user with
     * the given {@code userId} will not be able to see the user with the given
     * {@code blockedUserId} in their search results, and vice versa.
     *
     * @param userId        The ID of the user who is blocking another user.
     * @param blockedUserId The ID of the user who is being blocked.
     */
    void blockUser(Long userId, Long blockedUserId);

    /**
     * Removes the block relationship between the user with the given {@code userId} and
     * the user with the given {@code blockedUserId}.
     *
     * @param userId        The ID of the user who is blocking another user.
     * @param blockedUserId The ID of the user who is being unblocked.
     */
    void unblockUser(Long userId, Long blockedUserId);

    /**
     * Checks if a block relationship exists between the user with the given {@code userId}
     * and the user with the given {@code blockedUserId}.
     *
     * @param userId        The ID of the user who is blocking another user.
     * @param blockedUserId The ID of the user who is being blocked.
     * @return {@code true} if the block relationship exists, or {@code false} if it does not.
     */
    boolean blockExists(Long userId, Long blockedUserId);
}
