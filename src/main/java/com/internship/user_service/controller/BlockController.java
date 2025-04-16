package com.internship.user_service.controller;

import com.internship.user_service.service.BlockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/blocks")
@RequiredArgsConstructor
public class BlockController {
    private final BlockService blockService;

    /**
     * Blocks a user with the given {@code blockedUserId} for the user with the given {@code userId}.
     *
     * @param userId        The ID of the user who wants to block another user.
     * @param blockedUserId The ID of the user to be blocked.
     * @return A {@link ResponseEntity} with no content, indicating the block operation has been processed.
     */
    @PostMapping("/{userId}/{blockedUserId}")
    public ResponseEntity<Void> blockUser(@PathVariable Long userId,
                                          @PathVariable Long blockedUserId) {
        blockService.blockUser(userId, blockedUserId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Unblocks a user with the given {@code blockedUserId} for the user with the given {@code userId}.
     *
     * @param userId        The ID of the user who wants to unblock another user.
     * @param blockedUserId The ID of the user to be unblocked.
     * @return A {@link ResponseEntity} with no content, indicating the unblock operation has been processed.
     */
    @DeleteMapping("/{userId}/{blockedUserId}")
    public ResponseEntity<Void> unblockUser(@PathVariable Long userId,
                                            @PathVariable Long blockedUserId) {
        blockService.unblockUser(userId, blockedUserId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{blockingUserId}")
    public ResponseEntity<List<Long>> getBlockedUsersByUserId(
            @PathVariable Long blockingUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        return ResponseEntity.ok(blockService.getBlockedUsersByUserId(blockingUserId, page, pageSize));
    }
}
