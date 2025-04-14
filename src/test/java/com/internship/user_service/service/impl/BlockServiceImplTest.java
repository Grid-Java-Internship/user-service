package com.internship.user_service.service.impl;

import com.internship.user_service.exception.AlreadyExistsException;
import com.internship.user_service.exception.UserNotFoundException;
import com.internship.user_service.model.Block;
import com.internship.user_service.model.BlockId;
import com.internship.user_service.model.User;
import com.internship.user_service.repository.BlockRepository;
import com.internship.user_service.service.FavoriteService;
import com.internship.user_service.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlockServiceImplTest {
    @Mock
    private BlockRepository blockRepository;

    @Mock
    private UserService userService;

    @Mock
    private FavoriteService favoriteService;

    @InjectMocks
    private BlockServiceImpl blockService;

    private User user;
    private User blockedUser;
    private BlockId blockId;
    private Block block;

    private static final Long USER_ID = 1L;
    private static final Long BLOCKED_USER_ID = 2L;
    private static final Long NON_EXISTENT_USER_ID = 99L;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(USER_ID)
                .name("Blocking")
                .surname("User")
                .build();

        blockedUser = User.builder()
                .id(BLOCKED_USER_ID)
                .name("Blocked")
                .surname("User")
                .build();

        blockId = new BlockId(USER_ID, BLOCKED_USER_ID);

        block = new Block(blockId, user, blockedUser);
    }

    @Nested
    @DisplayName("blockUser Tests")
    class BlockUserTests {
        @Test
        @DisplayName("Should block user successfully when users exist, block does not exist, and favorite exists")
        void blockUser_success_withFavoriteDeletion() {
            // Arrange
            when(userService.getUserEntity(USER_ID)).thenReturn(user);
            when(userService.getUserEntity(BLOCKED_USER_ID)).thenReturn(blockedUser);
            when(blockRepository.existsById(blockId)).thenReturn(false);
            when(blockRepository.save(any(Block.class))).thenReturn(block);
            when(favoriteService.favoriteExists(USER_ID, BLOCKED_USER_ID)).thenReturn(true);
            doNothing().when(favoriteService).deleteFavorite(USER_ID, BLOCKED_USER_ID);

            // Act
            blockService.blockUser(USER_ID, BLOCKED_USER_ID);

            // Assert & Verify interactions
            verify(userService).getUserEntity(USER_ID);
            verify(userService).getUserEntity(BLOCKED_USER_ID);
            verify(blockRepository).existsById(blockId); // verify internal check
            verify(blockRepository).save(argThat(b ->
                    b.getId().equals(blockId) &&
                    b.getBlockingUser().equals(user) &&
                    b.getBlockedUser().equals(blockedUser)
            ));
            verify(favoriteService).favoriteExists(USER_ID, BLOCKED_USER_ID);
            verify(favoriteService).deleteFavorite(USER_ID, BLOCKED_USER_ID);
            verifyNoMoreInteractions(userService, blockRepository, favoriteService);
        }

        @Test
        @DisplayName("Should block user successfully when users exist, block does not exist, and favorite does not exist")
        void blockUser_success_withoutFavoriteDeletion() {
            // Arrange
            when(userService.getUserEntity(USER_ID)).thenReturn(user);
            when(userService.getUserEntity(BLOCKED_USER_ID)).thenReturn(blockedUser);
            when(blockRepository.existsById(blockId)).thenReturn(false);
            when(blockRepository.save(any(Block.class))).thenReturn(block);
            when(favoriteService.favoriteExists(USER_ID, BLOCKED_USER_ID)).thenReturn(false);

            // Act
            blockService.blockUser(USER_ID, BLOCKED_USER_ID);

            // Assert & Verify interactions
            verify(userService).getUserEntity(USER_ID);
            verify(userService).getUserEntity(BLOCKED_USER_ID);
            verify(blockRepository).existsById(blockId);
            verify(blockRepository).save(any(Block.class));
            verify(favoriteService).favoriteExists(USER_ID, BLOCKED_USER_ID);
            verify(favoriteService, never()).deleteFavorite(anyLong(), anyLong());
            verifyNoMoreInteractions(userService, blockRepository, favoriteService);
        }

        @Test
        @DisplayName("Should throw AlreadyExistsException when block already exists")
        void blockUser_alreadyExists() {
            // Arrange
            when(userService.getUserEntity(USER_ID)).thenReturn(user);
            when(userService.getUserEntity(BLOCKED_USER_ID)).thenReturn(blockedUser);
            when(blockRepository.existsById(blockId)).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> blockService.blockUser(USER_ID, BLOCKED_USER_ID))
                    .isInstanceOf(AlreadyExistsException.class)
                    .hasMessage("Block relationship already exists.");

            // Verify interactions
            verify(userService).getUserEntity(USER_ID);
            verify(userService).getUserEntity(BLOCKED_USER_ID);
            verify(blockRepository).existsById(blockId);
            verify(blockRepository, never()).save(any(Block.class));
            verifyNoMoreInteractions(userService, blockRepository);
            verifyNoInteractions(favoriteService);
        }

        @Test
        @DisplayName("Should throw UserNotFoundException when blocking user does not exist")
        void blockUser_userNotFound() {
            // Arrange
            when(userService.getUserEntity(NON_EXISTENT_USER_ID)).thenThrow(
                    new UserNotFoundException("User not found."));

            // Act & Assert
            assertThatThrownBy(() -> blockService.blockUser(NON_EXISTENT_USER_ID, BLOCKED_USER_ID))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("User not found.");

            // Verify interactions
            verify(userService).getUserEntity(NON_EXISTENT_USER_ID);
            verify(userService, never()).getUserEntity(BLOCKED_USER_ID);
            verifyNoMoreInteractions(userService);
            verifyNoInteractions(blockRepository, favoriteService);
        }

        @Test
        @DisplayName("Should throw UserNotFoundException when blocked user does not exist")
        void blockUser_blockedUserNotFound() {
            // Arrange
            when(userService.getUserEntity(USER_ID)).thenReturn(user);
            when(userService.getUserEntity(NON_EXISTENT_USER_ID)).thenThrow(
                    new UserNotFoundException("User not found."));

            // Act & Assert
            assertThatThrownBy(() -> blockService.blockUser(USER_ID, NON_EXISTENT_USER_ID))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("User not found.");

            // Verify interactions
            verify(userService).getUserEntity(USER_ID);
            verify(userService).getUserEntity(NON_EXISTENT_USER_ID);
            verifyNoInteractions(blockRepository, favoriteService);
            verifyNoMoreInteractions(userService);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException for null userId")
        void blockUser_nullUserId() {
            // Act & Assert
            assertThatThrownBy(() -> blockService.blockUser(null, BLOCKED_USER_ID))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Invalid userId or blockedUserId.");

            // Verify interactions
            verifyNoInteractions(userService, blockRepository, favoriteService);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException for null blockedUserId")
        void blockUser_nullBlockedUserId() {
            // Act & Assert
            assertThatThrownBy(() -> blockService.blockUser(USER_ID, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Invalid userId or blockedUserId.");

            // Verify no interactions happened
            verifyNoInteractions(userService, blockRepository, favoriteService);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when userId equals blockedUserId")
        void blockUser_sameUserId() {
            // Act & Assert
            assertThatThrownBy(() -> blockService.blockUser(USER_ID, USER_ID))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Invalid userId or blockedUserId.");

            // Verify no interactions happened
            verifyNoInteractions(userService, blockRepository, favoriteService);
        }
    }

    @Nested
    @DisplayName("unblockUser Tests")
    class UnblockUserTests {
        @Test
        @DisplayName("Should unblock user successfully when users and block exist")
        void unblockUser_success() {
            // Arrange
            when(userService.getUserEntity(USER_ID)).thenReturn(user);
            when(userService.getUserEntity(BLOCKED_USER_ID)).thenReturn(blockedUser);
            when(blockRepository.existsById(blockId)).thenReturn(true);
            doNothing().when(blockRepository).deleteById(blockId);

            // Act
            blockService.unblockUser(USER_ID, BLOCKED_USER_ID);

            // Assert & Verify interactions
            verify(userService).getUserEntity(USER_ID);
            verify(userService).getUserEntity(BLOCKED_USER_ID);
            verify(blockRepository).existsById(blockId);
            verify(blockRepository).deleteById(blockId);
            verifyNoMoreInteractions(userService, blockRepository);
            verifyNoInteractions(favoriteService);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when block does not exist")
        void unblockUser_doesNotExist() {
            // Arrange
            when(userService.getUserEntity(USER_ID)).thenReturn(user);
            when(userService.getUserEntity(BLOCKED_USER_ID)).thenReturn(blockedUser);
            when(blockRepository.existsById(blockId)).thenReturn(false);

            // Act & Assert
            assertThatThrownBy(() -> blockService.unblockUser(USER_ID, BLOCKED_USER_ID))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Block relationship does not exist.");

            // Verify interactions
            verify(userService).getUserEntity(USER_ID);
            verify(userService).getUserEntity(BLOCKED_USER_ID);
            verify(blockRepository).existsById(blockId);
            verify(blockRepository, never()).deleteById(any(BlockId.class));
            verifyNoMoreInteractions(userService, blockRepository);
            verifyNoInteractions(favoriteService);
        }

        @Test
        @DisplayName("Should throw UserNotFoundException when blocking user does not exist")
        void unblockUser_userNotFound() {
            // Arrange
            when(userService.getUserEntity(NON_EXISTENT_USER_ID)).thenThrow(
                    new UserNotFoundException("User not found."));

            // Act & Assert
            assertThatThrownBy(() -> blockService.unblockUser(NON_EXISTENT_USER_ID, BLOCKED_USER_ID))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("User not found.");

            // Verify interactions
            verify(userService).getUserEntity(NON_EXISTENT_USER_ID);
            verifyNoInteractions(blockRepository, favoriteService);
            verifyNoMoreInteractions(userService);
        }

        @Test
        @DisplayName("Should throw UserNotFoundException when blocked user does not exist")
        void unblockUser_blockedUserNotFound() {
            // Arrange
            when(userService.getUserEntity(USER_ID)).thenReturn(user);
            when(userService.getUserEntity(NON_EXISTENT_USER_ID)).thenThrow(
                    new UserNotFoundException("User not found."));

            // Act & Assert
            assertThatThrownBy(() -> blockService.unblockUser(USER_ID, NON_EXISTENT_USER_ID))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("User not found.");

            // Verify interactions
            verify(userService).getUserEntity(USER_ID);
            verify(userService).getUserEntity(NON_EXISTENT_USER_ID);
            verifyNoInteractions(blockRepository, favoriteService);
            verifyNoMoreInteractions(userService);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException for null userId")
        void unblockUser_nullUserId() {
            // Act & Assert
            assertThatThrownBy(() -> blockService.unblockUser(null, BLOCKED_USER_ID))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Invalid userId or blockedUserId.");

            // Verify no interactions happened
            verifyNoInteractions(userService, blockRepository, favoriteService);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException for null blockedUserId")
        void unblockUser_nullBlockedUserId() {
            // Act & Assert
            assertThatThrownBy(() -> blockService.unblockUser(USER_ID, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Invalid userId or blockedUserId.");

            // Verify no interactions happened
            verifyNoInteractions(userService, blockRepository, favoriteService);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when userId equals blockedUserId")
        void unblockUser_sameUserId() {
            // Act & Assert
            assertThatThrownBy(() -> blockService.unblockUser(USER_ID, USER_ID))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Invalid userId or blockedUserId.");

            // Verify no interactions happened
            verifyNoInteractions(userService, blockRepository, favoriteService);
        }
    }

    @Nested
    @DisplayName("blockExists Tests")
    class BlockExistsTests {
        @Test
        @DisplayName("Should return true when block exists")
        void blockExists_returnsTrue() {
            // Arrange
            when(blockRepository.existsById(blockId)).thenReturn(true);

            // Act
            boolean exists = blockService.blockExists(USER_ID, BLOCKED_USER_ID);

            // Assert
            assertThat(exists).isTrue();
            verify(blockRepository).existsById(blockId);
            verifyNoMoreInteractions(blockRepository);
            verifyNoInteractions(userService, favoriteService);
        }

        @Test
        @DisplayName("Should return false when block does not exist")
        void blockExists_returnsFalse() {
            // Arrange
            when(blockRepository.existsById(blockId)).thenReturn(false);

            // Act
            boolean exists = blockService.blockExists(USER_ID, BLOCKED_USER_ID);

            // Assert
            assertThat(exists).isFalse();
            verify(blockRepository).existsById(blockId);
            verifyNoMoreInteractions(blockRepository);
            verifyNoInteractions(userService, favoriteService);
        }
    }
}