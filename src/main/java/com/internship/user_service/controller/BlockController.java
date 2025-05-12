package com.internship.user_service.controller;

import com.internship.user_service.service.BlockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/blocks")
@RequiredArgsConstructor
public class BlockController {
    private final BlockService blockService;

    /**
     * Blocks a user with the given {@code blockedUserId} for the user with the given {@code userId}.
     *
     * @param blockedUserId The ID of the user to be blocked.
     * @return A {@link ResponseEntity} with no content, indicating the block operation has been processed.
     */
    @PostMapping("/{blockedUserId}")
    public ResponseEntity<Void> blockUser(@PathVariable Long blockedUserId) {
        blockService.blockUser(blockedUserId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Unblocks a user with the given {@code blockedUserId} for the user with the given {@code userId}.
     *
     * @param userId        The ID of the user who wants to unblock another user.
     * @param blockedUserId The ID of the user to be unblocked.
     * @return A {@link ResponseEntity} with no content, indicating the unblock operation has been processed.
     */
    @DeleteMapping("/{blockedUserId}")
    public ResponseEntity<Void> unblockUser(@PathVariable Long blockedUserId) {
        blockService.unblockUser(blockedUserId);
        return ResponseEntity.noContent().build();
    }
}
