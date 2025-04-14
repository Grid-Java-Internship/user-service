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
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;

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

    @Captor
    private ArgumentCaptor<Pageable> pageableCaptor;

    private User user;
    private User blockedUser;
    private BlockId blockId;
    private Block block;

    private User blockedUser1;
    private User blockedUser2;
    private BlockId blockId1;
    private BlockId blockId2;
    private Block block1;
    private Block block2;


    private static final Long USER_ID = 1L;

    private static final Long ORIGINAL_BLOCKED_USER_ID = 4L;
    private static final Long NON_EXISTENT_USER_ID = 99L;

    private static final Long BLOCKED_USER_ID_1 = 2L;
    private static final Long BLOCKED_USER_ID_2 = 3L;


    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(USER_ID)
                .name("Blocking")
                .surname("User")
                .build();

        blockedUser = User.builder()
                .id(ORIGINAL_BLOCKED_USER_ID)
                .name("BlockedOriginal")
                .surname("User")
                .build();

        blockId = new BlockId(USER_ID, ORIGINAL_BLOCKED_USER_ID);
        block = new Block(blockId, user, blockedUser);

        blockedUser1 = User.builder()
                .id(BLOCKED_USER_ID_1)
                .name("Blocked")
                .surname("User1")
                .build();
        blockId1 = new BlockId(USER_ID, BLOCKED_USER_ID_1);
        block1 = new Block(blockId1, user, blockedUser1);

        blockedUser2 = User.builder()
                .id(BLOCKED_USER_ID_2)
                .name("Blocked")
                .surname("User2")
                .build();
        blockId2 = new BlockId(USER_ID, BLOCKED_USER_ID_2);
        block2 = new Block(blockId2, user, blockedUser2);
    }


    @Nested
    @DisplayName("blockUser Tests")
    class BlockUserTests {

        @Test
        @DisplayName("Should block user successfully when users exist, block does not exist, and favorite exists")
        void blockUser_success_withFavoriteDeletion() {

            when(userService.getUserEntity(USER_ID)).thenReturn(user);
            when(userService.getUserEntity(ORIGINAL_BLOCKED_USER_ID)).thenReturn(blockedUser);
            when(blockRepository.existsById(blockId)).thenReturn(false); // Koristi blockId (1L, 4L)
            when(blockRepository.save(any(Block.class))).thenReturn(block); // Koristi block (sa ID 1L, 4L)
            when(favoriteService.favoriteExists(USER_ID, ORIGINAL_BLOCKED_USER_ID)).thenReturn(true);
            doNothing().when(favoriteService).deleteFavorite(USER_ID, ORIGINAL_BLOCKED_USER_ID);

            // Act
            blockService.blockUser(USER_ID, ORIGINAL_BLOCKED_USER_ID);

            // Assert & Verify interactions
            verify(userService).getUserEntity(USER_ID);
            verify(userService).getUserEntity(ORIGINAL_BLOCKED_USER_ID);
            verify(blockRepository).existsById(blockId);
            verify(blockRepository).save(argThat(b ->
                    b.getId().equals(blockId) &&
                            b.getBlockingUser().equals(user) &&
                            b.getBlockedUser().equals(blockedUser)
            ));
            verify(favoriteService).favoriteExists(USER_ID, ORIGINAL_BLOCKED_USER_ID);
            verify(favoriteService).deleteFavorite(USER_ID, ORIGINAL_BLOCKED_USER_ID);
            verifyNoMoreInteractions(userService, blockRepository, favoriteService);
        }

        @Test
        @DisplayName("Should block user successfully when users exist, block does not exist, and favorite does not exist")
        void blockUser_success_withoutFavoriteDeletion() {
            // Arrange
            when(userService.getUserEntity(USER_ID)).thenReturn(user);
            when(userService.getUserEntity(ORIGINAL_BLOCKED_USER_ID)).thenReturn(blockedUser);
            when(blockRepository.existsById(blockId)).thenReturn(false);
            when(blockRepository.save(any(Block.class))).thenReturn(block);
            when(favoriteService.favoriteExists(USER_ID, ORIGINAL_BLOCKED_USER_ID)).thenReturn(false);

            // Act
            blockService.blockUser(USER_ID, ORIGINAL_BLOCKED_USER_ID);

            // Assert & Verify interactions
            verify(userService).getUserEntity(USER_ID);
            verify(userService).getUserEntity(ORIGINAL_BLOCKED_USER_ID);
            verify(blockRepository).existsById(blockId);
            verify(blockRepository).save(any(Block.class)); // Može i argThat ako želiš preciznije
            verify(favoriteService).favoriteExists(USER_ID, ORIGINAL_BLOCKED_USER_ID);
            verify(favoriteService, never()).deleteFavorite(anyLong(), anyLong());
            verifyNoMoreInteractions(userService, blockRepository, favoriteService);
        }

        @Test
        @DisplayName("Should throw AlreadyExistsException when block already exists")
        void blockUser_alreadyExists() {
            // Arrange
            when(userService.getUserEntity(USER_ID)).thenReturn(user);
            when(userService.getUserEntity(ORIGINAL_BLOCKED_USER_ID)).thenReturn(blockedUser);
            when(blockRepository.existsById(blockId)).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> blockService.blockUser(USER_ID, ORIGINAL_BLOCKED_USER_ID))
                    .isInstanceOf(AlreadyExistsException.class)
                    .hasMessage("Block relationship already exists.");

            // Verify interactions
            verify(userService).getUserEntity(USER_ID);
            verify(userService).getUserEntity(ORIGINAL_BLOCKED_USER_ID);
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
            assertThatThrownBy(() -> blockService.blockUser(NON_EXISTENT_USER_ID, ORIGINAL_BLOCKED_USER_ID))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("User not found.");

            // Verify interactions
            verify(userService).getUserEntity(NON_EXISTENT_USER_ID);
            verify(userService, never()).getUserEntity(ORIGINAL_BLOCKED_USER_ID); // Proveri da ovo nije pozvano
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
            assertThatThrownBy(() -> blockService.blockUser(null, ORIGINAL_BLOCKED_USER_ID))
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
            when(userService.getUserEntity(ORIGINAL_BLOCKED_USER_ID)).thenReturn(blockedUser);
            when(blockRepository.existsById(blockId)).thenReturn(true);
            doNothing().when(blockRepository).deleteById(blockId);

            // Act
            blockService.unblockUser(USER_ID, ORIGINAL_BLOCKED_USER_ID);

            // Assert & Verify interactions
            verify(userService).getUserEntity(USER_ID);
            verify(userService).getUserEntity(ORIGINAL_BLOCKED_USER_ID);
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
            when(userService.getUserEntity(ORIGINAL_BLOCKED_USER_ID)).thenReturn(blockedUser);
            when(blockRepository.existsById(blockId)).thenReturn(false);

            // Act & Assert
            assertThatThrownBy(() -> blockService.unblockUser(USER_ID, ORIGINAL_BLOCKED_USER_ID))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Block relationship does not exist.");

            // Verify interactions
            verify(userService).getUserEntity(USER_ID);
            verify(userService).getUserEntity(ORIGINAL_BLOCKED_USER_ID);
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
            assertThatThrownBy(() -> blockService.unblockUser(NON_EXISTENT_USER_ID, ORIGINAL_BLOCKED_USER_ID))
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
            assertThatThrownBy(() -> blockService.unblockUser(null, ORIGINAL_BLOCKED_USER_ID))
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
            boolean exists = blockService.blockExists(USER_ID, ORIGINAL_BLOCKED_USER_ID);

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
            boolean exists = blockService.blockExists(USER_ID, ORIGINAL_BLOCKED_USER_ID);

            // Assert
            assertThat(exists).isFalse();
            verify(blockRepository).existsById(blockId);
            verifyNoMoreInteractions(blockRepository);
            verifyNoInteractions(userService, favoriteService);
        }
    }



    @Nested
    @DisplayName("getBlockedUsersByUserId Tests")
    class GetBlockedUsersByUserIdTests {

        private final int PAGE_NUM = 0;
        private final int PAGE_SIZE = 10;

        @Test
        @DisplayName("Should return list of blocked user IDs when user exists and has blocked users")
        void getBlockedUsers_success_usersFound() {
            // Arrange

            List<Block> blocks = Arrays.asList(block1, block2);
            Pageable pageable = PageRequest.of(PAGE_NUM, PAGE_SIZE);
            Page<Block> blockPage = new PageImpl<>(blocks, pageable, blocks.size());
            List<Long> expectedBlockedIds = Arrays.asList(BLOCKED_USER_ID_1, BLOCKED_USER_ID_2);

            when(userService.getUserEntity(USER_ID)).thenReturn(user);
            when(blockRepository.findByBlockingUser(user, pageable)).thenReturn(blockPage);

            // Act
            List<Long> actualBlockedIds = blockService.getBlockedUsersByUserId(USER_ID, PAGE_NUM, PAGE_SIZE);

            // Assert
            assertThat(actualBlockedIds)
                    .isNotNull()
                    .hasSize(expectedBlockedIds.size())
                    .containsExactlyInAnyOrderElementsOf(expectedBlockedIds);

            // Verify interactions
            verify(userService).getUserEntity(USER_ID);
            verify(blockRepository).findByBlockingUser(user, pageable);
            verifyNoMoreInteractions(userService, blockRepository);
            verifyNoInteractions(favoriteService);
        }

        @Test
        @DisplayName("Should return empty list when user exists but has not blocked any users")
        void getBlockedUsers_success_noUsersFound() {
            // Arrange
            Pageable pageable = PageRequest.of(PAGE_NUM, PAGE_SIZE);
            Page<Block> emptyBlockPage = Page.empty(pageable);

            when(userService.getUserEntity(USER_ID)).thenReturn(user);
            when(blockRepository.findByBlockingUser(user, pageable)).thenReturn(emptyBlockPage);

            // Act
            List<Long> actualBlockedIds = blockService.getBlockedUsersByUserId(USER_ID, PAGE_NUM, PAGE_SIZE);

            // Assert
            assertThat(actualBlockedIds)
                    .isNotNull()
                    .isEmpty();

            // Verify interactions
            verify(userService).getUserEntity(USER_ID);
            verify(blockRepository).findByBlockingUser(user, pageable);
            verifyNoMoreInteractions(userService, blockRepository);
            verifyNoInteractions(favoriteService);
        }

        @Test
        @DisplayName("Should throw UserNotFoundException when the blocking user does not exist")
        void getBlockedUsers_userNotFound() {
            // Arrange
            when(userService.getUserEntity(NON_EXISTENT_USER_ID)).thenThrow(
                    new UserNotFoundException("User not found with id: " + NON_EXISTENT_USER_ID));

            // Act & Assert
            assertThatThrownBy(() -> blockService.getBlockedUsersByUserId(NON_EXISTENT_USER_ID, PAGE_NUM, PAGE_SIZE))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessageContaining("User not found");

            // Verify interactions
            verify(userService).getUserEntity(NON_EXISTENT_USER_ID);
            verify(blockRepository, never()).findByBlockingUser(any(User.class), any(Pageable.class));
            verifyNoMoreInteractions(userService);
            verifyNoInteractions(blockRepository, favoriteService);
        }

        @Test
        @DisplayName("Should pass correct Pageable to repository based on input page and pageSize")
        void getBlockedUsers_correctPageableUsed() {
            // Arrange
            int specificPage = 2;
            int specificSize = 5;
            Pageable expectedPageable = PageRequest.of(specificPage, specificSize);

            when(userService.getUserEntity(USER_ID)).thenReturn(user);
            when(blockRepository.findByBlockingUser(eq(user), any(Pageable.class))).thenReturn(Page.empty(expectedPageable));

            // Act
            blockService.getBlockedUsersByUserId(USER_ID, specificPage, specificSize);

            // Assert
            verify(blockRepository).findByBlockingUser(eq(user), pageableCaptor.capture());
            Pageable actualPageable = pageableCaptor.getValue();

            assertThat(actualPageable.getPageNumber()).isEqualTo(specificPage);
            assertThat(actualPageable.getPageSize()).isEqualTo(specificSize);

            // Verify other interactions
            verify(userService).getUserEntity(USER_ID);
            verifyNoMoreInteractions(userService, blockRepository);
            verifyNoInteractions(favoriteService);
        }
    }

}