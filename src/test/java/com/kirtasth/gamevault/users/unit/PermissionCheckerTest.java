package com.kirtasth.gamevault.users.unit;

import com.kirtasth.gamevault.cart.domain.models.CartItem;
import com.kirtasth.gamevault.cart.domain.models.ShoppingCart;
import com.kirtasth.gamevault.cart.domain.ports.out.CartRepoPort;
import com.kirtasth.gamevault.catalog.domain.ports.in.GameServicePort;
import com.kirtasth.gamevault.common.domain.models.enums.RoleEnum;
import com.kirtasth.gamevault.users.domain.models.AuthUser;
import com.kirtasth.gamevault.users.domain.models.Role;
import com.kirtasth.gamevault.users.infrastructure.security.PermissionChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermissionCheckerTest {

    @Mock
    private GameServicePort gameServicePort;

    @Mock
    private CartRepoPort cartRepoPort;

    @Mock
    private Authentication authentication;

    @Mock
    private RequestAuthorizationContext requestAuthorizationContext;

    @InjectMocks
    private PermissionChecker permissionChecker;

    private AuthUser devUser;
    private AuthUser regularUser;
    private Supplier<Authentication> authSupplier;

    @BeforeEach
    void setUp() {
        Role devRole = new Role();
        devRole.setRole(RoleEnum.DEVELOPER);

        devUser = new AuthUser();
        devUser.setId(1L);
        devUser.setRoles(List.of(devRole));

        regularUser = new AuthUser();
        regularUser.setId(2L);
        regularUser.setRoles(List.of());

        authSupplier = () -> authentication;
    }

    @Test
    void isGameOwner_whenUserIsDeveloperAndOwnsGame_returnsTrue() {
        // Arrange
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(devUser);
        when(requestAuthorizationContext.getVariables()).thenReturn(Map.of("gameId", "100"));
        when(gameServicePort.isDeveloperOfGame(1L, 100L)).thenReturn(true);

        // Act
        var result = permissionChecker.isGameOwner("gameId").authorize(authSupplier, requestAuthorizationContext);

        // Assert
        assertTrue(result.isGranted());
    }

    @Test
    void isGameOwner_whenUserIsDeveloperAndDoesNotOwnGame_returnsFalse() {
        // Arrange
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(devUser);
        when(requestAuthorizationContext.getVariables()).thenReturn(Map.of("gameId", "100"));
        when(gameServicePort.isDeveloperOfGame(1L, 100L)).thenReturn(false);

        // Act
        var result = permissionChecker.isGameOwner("gameId").authorize(authSupplier, requestAuthorizationContext);

        // Assert
        assertFalse(result.isGranted());
    }

    @Test
    void isGameOwner_whenUserIsNotDeveloper_returnsFalse() {
        // Arrange
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(regularUser);

        // Act
        var result = permissionChecker.isGameOwner("gameId").authorize(authSupplier, requestAuthorizationContext);

        // Assert
        assertFalse(result.isGranted());
    }

    @Test
    void isGameOwner_whenNotAuthenticated_returnsFalse() {
        // Arrange
        when(authentication.isAuthenticated()).thenReturn(false);

        // Act
        var result = permissionChecker.isGameOwner("gameId").authorize(authSupplier, requestAuthorizationContext);

        // Assert
        assertFalse(result.isGranted());
    }

    @Test
    void isCartItemOwner_whenUserOwnsCartItem_returnsTrue() {
        // Arrange
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(regularUser);
        when(requestAuthorizationContext.getVariables()).thenReturn(Map.of("itemId", "500"));

        CartItem cartItem = CartItem.builder().id(500L).cartId(10L).build();
        ShoppingCart cart = ShoppingCart.builder().id(10L).userId(2L).build();

        when(cartRepoPort.findById(500L)).thenReturn(cartItem);
        when(cartRepoPort.findOpenedByUserId(2L)).thenReturn(Optional.of(cart));

        // Act
        var result = permissionChecker.isCartItemOwner("itemId").authorize(authSupplier, requestAuthorizationContext);

        // Assert
        assertTrue(result.isGranted());
    }

    @Test
    void isCartItemOwner_whenUserDoesNotOwnCartItem_returnsFalse() {
        // Arrange
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(regularUser);
        when(requestAuthorizationContext.getVariables()).thenReturn(Map.of("itemId", "500"));

        CartItem cartItem = CartItem.builder().id(500L).cartId(11L).build(); // Different cart
        ShoppingCart cart = ShoppingCart.builder().id(10L).userId(2L).build();

        when(cartRepoPort.findById(500L)).thenReturn(cartItem);
        when(cartRepoPort.findOpenedByUserId(2L)).thenReturn(Optional.of(cart));

        // Act
        var result = permissionChecker.isCartItemOwner("itemId").authorize(authSupplier, requestAuthorizationContext);

        // Assert
        assertFalse(result.isGranted());
    }

    @Test
    void isCartItemOwner_whenCartItemNotFound_returnsFalse() {
        // Arrange
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(regularUser);
        when(requestAuthorizationContext.getVariables()).thenReturn(Map.of("itemId", "500"));

        when(cartRepoPort.findById(500L)).thenReturn(null);

        // Act
        var result = permissionChecker.isCartItemOwner("itemId").authorize(authSupplier, requestAuthorizationContext);

        // Assert
        assertFalse(result.isGranted());
    }
}
