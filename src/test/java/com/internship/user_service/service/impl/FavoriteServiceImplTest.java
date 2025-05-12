package com.internship.user_service.service.impl;

import com.internship.user_service.dto.FavoriteResponse;
import com.internship.user_service.exception.AlreadyExistsException;
import com.internship.user_service.exception.UserNotFoundException;
import com.internship.user_service.mapper.FavoriteMapper;
import com.internship.user_service.model.Favorite;
import com.internship.user_service.model.FavoriteId;
import com.internship.user_service.model.User;
import com.internship.user_service.repository.FavoriteRepository;
import com.internship.user_service.service.BlockService;
import com.internship.user_service.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceImplTest {
    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private FavoriteMapper favoriteMapper;

    @Mock
    private UserService userService;

    @Mock
    private BlockService blockService;

    @InjectMocks
    private FavoriteServiceImpl favoriteService;

    private User user;
    private User favoriteUser;
    private FavoriteId favoriteId;
    private Favorite favorite;
    private FavoriteResponse favoriteResponse;

    Authentication authentication = mock(Authentication.class);
    SecurityContext securityContext = mock(SecurityContext.class);

    private static final Long USER_ID = 1L;
    private static final Long FAVORITE_USER_ID = 2L;
    private static final Long NON_EXISTENT_USER_ID = 99L;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(USER_ID)
                .name("Test")
                .surname("User")
                .build();

        favoriteUser = User.builder()
                .id(FAVORITE_USER_ID)
                .name("Favorite")
                .surname("User")
                .build();

        favoriteId = new FavoriteId(USER_ID, FAVORITE_USER_ID);

        favorite = new Favorite(favoriteId, user, favoriteUser);

        favoriteResponse = FavoriteResponse.builder()
                .id(favoriteId)
                .build();
        when(authentication.getPrincipal()).thenReturn("1");
        Collection authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        when(authentication.getAuthorities()).thenReturn(authorities);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Nested
    @DisplayName("getFavoriteUsers Tests")
    class GetFavoriteUsersTests {
        @Test
        @DisplayName("Should get favorite users successfully")
        void getFavoriteUsers_success() {
            // Arrange
            when(userService.getUserEntity(USER_ID)).thenReturn(user);
            when(favoriteRepository.findByUser(any(User.class), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(favorite)));

            // Act
            List<Long> result = favoriteService.getFavoriteUsers(0, 10);

            // Assert
            assertThat(result)
                    .isNotNull()
                    .hasSize(1)
                    .contains(FAVORITE_USER_ID);

            // Verify interactions
            verify(userService).getUserEntity(USER_ID);
            verify(favoriteRepository).findByUser(any(User.class), any(Pageable.class));
            verifyNoMoreInteractions(userService, favoriteRepository);
        }

        @Test
        @DisplayName("Should throw UserNotFoundException when user does not exist")
        void getFavoriteUsers_userNotFound() {
            // Arrange
            when(userService.getUserEntity(USER_ID)).thenThrow(
                    new UserNotFoundException("User not found."));

            // Act & Assert
            assertThatThrownBy(() -> favoriteService.getFavoriteUsers(0, 10))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("User not found.");

            // Verify interactions
            verify(userService).getUserEntity(USER_ID);
            verifyNoMoreInteractions(userService);
            verifyNoInteractions(favoriteRepository);
        }

        @Test
        @DisplayName("Should return empty list when user has no favorite users")
        void getFavoriteUsers_noFavoriteUsers() {
            // Arrange
            when(userService.getUserEntity(USER_ID)).thenReturn(user);
            when(favoriteRepository.findByUser(any(User.class), any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

            // Act
            List<Long> result = favoriteService.getFavoriteUsers(0, 10);

            // Assert
            assertThat(result).isEmpty();

            // Verify interactions
            verify(userService).getUserEntity(USER_ID);
            verify(favoriteRepository).findByUser(any(User.class), any(Pageable.class));
            verifyNoMoreInteractions(userService, favoriteRepository);
        }
    }

    @Nested
    @DisplayName("addFavorite Tests")
    class AddFavoriteTests {
        @Test
        @DisplayName("Should add favorite successfully when users exist and favorite does not")
        void addFavorite_success() {
            // Arrange
            when(userService.getUserEntity(USER_ID)).thenReturn(user);
            when(userService.getUserEntity(FAVORITE_USER_ID)).thenReturn(favoriteUser);
            when(blockService.blockExists(USER_ID, FAVORITE_USER_ID)).thenReturn(false);
            when(favoriteRepository.existsById(favoriteId)).thenReturn(false);
            when(favoriteRepository.save(any(Favorite.class))).thenReturn(favorite); // Return the created favorite
            when(favoriteMapper.toResponse(favorite)).thenReturn(favoriteResponse);

            // Act
            FavoriteResponse result = favoriteService.addFavorite(FAVORITE_USER_ID);

            // Assert
            assertThat(result)
                    .isNotNull()
                    .isEqualTo(favoriteResponse);
            assertThat(result.getId()).isEqualTo(favoriteId);

            // Verify interactions
            verify(userService).getUserEntity(USER_ID);
            verify(userService).getUserEntity(FAVORITE_USER_ID);
            verify(blockService).blockExists(USER_ID, FAVORITE_USER_ID);
            verify(favoriteRepository).existsById(favoriteId);
            verify(favoriteRepository).save(argThat(fav ->
                    fav.getId().equals(favoriteId) &&
                    fav.getUser().equals(user) &&
                    fav.getFavoriteUser().equals(favoriteUser)
            ));
            verify(favoriteMapper).toResponse(favorite);
            verifyNoMoreInteractions(userService, blockService, favoriteRepository, favoriteMapper);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException if favoriteUserId is blocked by userId")
        void addFavorite_blocked() {
            // Arrange
            when(userService.getUserEntity(USER_ID)).thenReturn(user);
            when(userService.getUserEntity(FAVORITE_USER_ID)).thenReturn(favoriteUser);
            when(blockService.blockExists(USER_ID, FAVORITE_USER_ID)).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> favoriteService.addFavorite(FAVORITE_USER_ID))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("User blocked user to be favorited.");

            // Verify interactions (save and map should not be called)
            verify(userService).getUserEntity(USER_ID);
            verify(userService).getUserEntity(FAVORITE_USER_ID);
            verify(blockService).blockExists(USER_ID, FAVORITE_USER_ID);
            verifyNoInteractions(favoriteRepository, favoriteMapper);
            verifyNoMoreInteractions(userService, blockService);
        }

        @Test
        @DisplayName("Should throw AlreadyExistsException when favorite already exists")
        void addFavorite_alreadyExists() {
            // Arrange
            when(userService.getUserEntity(USER_ID)).thenReturn(user);
            when(userService.getUserEntity(FAVORITE_USER_ID)).thenReturn(favoriteUser);
            when(blockService.blockExists(USER_ID, FAVORITE_USER_ID)).thenReturn(false);
            when(favoriteRepository.existsById(favoriteId)).thenReturn(false);
            when(favoriteRepository.existsById(favoriteId)).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> favoriteService.addFavorite(FAVORITE_USER_ID))
                    .isInstanceOf(AlreadyExistsException.class)
                    .hasMessage("Favorite relationship already exists.");

            // Verify interactions (save and map should not be called)
            verify(userService).getUserEntity(USER_ID);
            verify(userService).getUserEntity(FAVORITE_USER_ID);
            verify(blockService).blockExists(USER_ID, FAVORITE_USER_ID);
            verify(favoriteRepository).existsById(favoriteId);
            verifyNoInteractions(favoriteMapper);
            verifyNoMoreInteractions(userService, blockService, favoriteRepository);
        }

        @Test
        @DisplayName("Should throw UserNotFoundException when user does not exist")
        void addFavorite_userNotFound() {
            // Arrange
            when(userService.getUserEntity(anyLong())).thenThrow(
                    new UserNotFoundException("User not found."));

            // Act & Assert
            assertThatThrownBy(() -> favoriteService.addFavorite(FAVORITE_USER_ID))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("User not found.");

            // Verify interactions
            verify(userService).getUserEntity(anyLong());
            verify(userService, never()).getUserEntity(FAVORITE_USER_ID);
            verifyNoInteractions(blockService, favoriteRepository, favoriteMapper);
            verifyNoMoreInteractions(userService);
        }

        @Test
        @DisplayName("Should throw UserNotFoundException when favorite user does not exist")
        void addFavorite_favoriteUserNotFound() {
            // Arrange
            when(userService.getUserEntity(USER_ID)).thenReturn(user);
            when(userService.getUserEntity(NON_EXISTENT_USER_ID)).thenThrow(
                    new UserNotFoundException("User not found."));

            // Act & Assert
            assertThatThrownBy(() -> favoriteService.addFavorite(NON_EXISTENT_USER_ID))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("User not found.");

            // Verify interactions
            verify(userService).getUserEntity(USER_ID);
            verify(userService).getUserEntity(NON_EXISTENT_USER_ID);
            verifyNoInteractions(blockService, favoriteRepository, favoriteMapper);
            verifyNoMoreInteractions(userService);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException for null userId")
        void addFavorite_nullUserId() {
            // Act & Assert
            when(authentication.getPrincipal()).thenReturn(null);
            Collection authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
            when(authentication.getAuthorities()).thenReturn(authorities);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            SecurityContextHolder.setContext(securityContext);

            assertThatThrownBy(() -> favoriteService.addFavorite(FAVORITE_USER_ID))
                    .isInstanceOf(NumberFormatException.class);

            // Verify interactions
            verifyNoInteractions(userService, blockService, favoriteRepository, favoriteMapper);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException for null favoriteUserId")
        void addFavorite_nullFavoriteUserId() {
            // Act & Assert
            assertThatThrownBy(() -> favoriteService.addFavorite(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Invalid userId or favoriteUserId.");

            // Verify no repository/mapper interactions happened
            verifyNoInteractions(userService, blockService, favoriteRepository, favoriteMapper);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when userId equals favoriteUserId")
        void addFavorite_sameUserId() {
            // Act & Assert
            assertThatThrownBy(() -> favoriteService.addFavorite(USER_ID))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Invalid userId or favoriteUserId.");

            // Verify no repository/mapper interactions happened
            verifyNoInteractions(userService, blockService, favoriteRepository, favoriteMapper);
        }
    }

    @Nested
    @DisplayName("deleteFavorite Tests")
    class DeleteFavoriteTests {

        @Test
        @DisplayName("Should delete favorite successfully when users and favorite exist")
        void deleteFavorite_success() {
            // Arrange
            when(userService.getUserEntity(USER_ID)).thenReturn(user);
            when(userService.getUserEntity(FAVORITE_USER_ID)).thenReturn(favoriteUser);
            when(favoriteRepository.existsById(favoriteId)).thenReturn(true);
            doNothing().when(favoriteRepository).deleteById(favoriteId);

            // Act
            favoriteService.deleteFavorite(FAVORITE_USER_ID);

            // Assert & Verify interactions
            verify(userService).getUserEntity(USER_ID);
            verify(userService).getUserEntity(FAVORITE_USER_ID);
            verify(favoriteRepository).existsById(favoriteId);
            verify(favoriteRepository).deleteById(favoriteId);
            verifyNoMoreInteractions(userService, favoriteRepository);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when favorite does not exist")
        void deleteFavorite_doesNotExist() {
            // Arrange
            when(userService.getUserEntity(USER_ID)).thenReturn(user);
            when(userService.getUserEntity(FAVORITE_USER_ID)).thenReturn(favoriteUser);
            when(favoriteRepository.existsById(favoriteId)).thenReturn(false);

            // Act & Assert
            assertThatThrownBy(() -> favoriteService.deleteFavorite(FAVORITE_USER_ID))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Favorite relationship does not exist.");

            // Verify interactions (delete should not be called)
            verify(userService).getUserEntity(USER_ID);
            verify(userService).getUserEntity(FAVORITE_USER_ID);
            verify(favoriteRepository).existsById(favoriteId);
            verifyNoMoreInteractions(userService, favoriteRepository);
        }

        @Test
        @DisplayName("Should throw UserNotFoundException when user does not exist")
        void deleteFavorite_userNotFound() {
            // Arrange
            when(userService.getUserEntity(anyLong())).thenThrow(
                    new UserNotFoundException("User not found."));

            // Act & Assert
            assertThatThrownBy(() -> favoriteService.deleteFavorite(FAVORITE_USER_ID))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("User not found.");

            // Verify interactions
            verify(userService).getUserEntity(anyLong());
            verify(userService, never()).getUserEntity(FAVORITE_USER_ID);
            verifyNoInteractions(favoriteRepository);
            verifyNoMoreInteractions(userService);
        }

        @Test
        @DisplayName("Should throw UserNotFoundException when favorite user does not exist")
        void deleteFavorite_favoriteUserNotFound() {
            // Arrange
            when(userService.getUserEntity(USER_ID)).thenReturn(user);
            when(userService.getUserEntity(NON_EXISTENT_USER_ID)).thenThrow(
                    new UserNotFoundException("User not found."));

            // Act & Assert
            assertThatThrownBy(() -> favoriteService.deleteFavorite(NON_EXISTENT_USER_ID))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("User not found.");

            // Verify interactions
            verify(userService).getUserEntity(USER_ID);
            verify(userService).getUserEntity(NON_EXISTENT_USER_ID);
            verifyNoInteractions(favoriteRepository);
            verifyNoMoreInteractions(userService);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException for null userId")
        void deleteFavorite_nullUserId() {
            // Act & Assert
            when(authentication.getPrincipal()).thenReturn(null);
            Collection authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
            when(authentication.getAuthorities()).thenReturn(authorities);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            SecurityContextHolder.setContext(securityContext);
            assertThatThrownBy(() -> favoriteService.deleteFavorite(FAVORITE_USER_ID))
                    .isInstanceOf(NumberFormatException.class);

            // Verify no repository interactions happened
            verifyNoInteractions(userService, favoriteRepository);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException for null favoriteUserId")
        void deleteFavorite_nullFavoriteUserId() {
            // Act & Assert
            assertThatThrownBy(() -> favoriteService.deleteFavorite(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Invalid userId or favoriteUserId.");

            // Verify no repository interactions happened
            verifyNoInteractions(userService, favoriteRepository);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when userId equals favoriteUserId")
        void deleteFavorite_sameUserId() {
            // Act & Assert
            assertThatThrownBy(() -> favoriteService.deleteFavorite(USER_ID))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Invalid userId or favoriteUserId.");

            // Verify no repository interactions happened
            verifyNoInteractions(userService, favoriteRepository);
        }
    }
}