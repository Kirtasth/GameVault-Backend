package com.kirtasth.gamevault.users.unit;

import com.kirtasth.gamevault.common.domain.models.enums.RoleEnum;
import com.kirtasth.gamevault.users.application.exception.UserNotFoundException;
import com.kirtasth.gamevault.users.application.services.UserServiceAdapter;
import com.kirtasth.gamevault.users.domain.models.NewUser;
import com.kirtasth.gamevault.users.domain.models.Role;
import com.kirtasth.gamevault.users.domain.models.UpdatedUser;
import com.kirtasth.gamevault.users.domain.models.User;
import com.kirtasth.gamevault.users.domain.ports.out.UserRepoPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceAdapterTest {

    @Mock
    private UserRepoPort userRepo;

    @InjectMocks
    private UserServiceAdapter userServiceAdapter;

    @Test
    void getUserByEmail_ShouldReturnUser_WhenExists() {
        User user = new User();
        user.setEmail("test@example.com");
        when(userRepo.findUserByEmail("test@example.com")).thenReturn(Optional.of(user));

        User result = userServiceAdapter.getUserByEmail("test@example.com");

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void getUserByEmail_ShouldThrowException_WhenNotExists() {
        when(userRepo.findUserByEmail("notfound@example.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userServiceAdapter.getUserByEmail("notfound@example.com"));
    }

    @Test
    void saveUser_ShouldCreateUserWithDefaults() {
        NewUser newUser = new NewUser();
        newUser.setUsername("testuser");
        newUser.setEmail("test@example.com");
        newUser.setPassword("password");

        Role role = new Role();
        role.setRole(RoleEnum.USER);
        
        when(userRepo.findRole(RoleEnum.USER)).thenReturn(role);
        when(userRepo.saveUser(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        User savedUser = userServiceAdapter.saveUser(newUser);

        assertNotNull(savedUser);
        assertEquals("testuser", savedUser.getUsername());
        assertTrue(savedUser.isAccountEnabled());
        assertFalse(savedUser.isAccountExpired());
        assertEquals(1, savedUser.getRoles().size());
        assertEquals(RoleEnum.USER, savedUser.getRoles().getFirst().getRole());
    }

    @Test
    void updateUser_ShouldUpdateOnlyProvidedFields() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("olduser");
        existingUser.setEmail("old@example.com");

        UpdatedUser updatedUser = new UpdatedUser();
        updatedUser.setUsername("newuser");

        when(userRepo.findUserById(1L)).thenReturn(existingUser);
        when(userRepo.saveUser(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        User result = userServiceAdapter.updateUser(1L, updatedUser);

        assertEquals("newuser", result.getUsername());
        assertEquals("old@example.com", result.getEmail());
    }

    @Test
    void canCreateGames_ShouldReturnTrue_WhenUserIsAdminOrDeveloper() {
        Role adminRole = new Role();
        adminRole.setRole(RoleEnum.ADMIN);
        
        when(userRepo.findRolesByUserId(1L)).thenReturn(List.of(adminRole));

        assertTrue(userServiceAdapter.canCreateGames(1L));
    }

    @Test
    void canCreateGames_ShouldReturnFalse_WhenUserIsOnlyUser() {
        Role userRole = new Role();
        userRole.setRole(RoleEnum.USER);
        
        when(userRepo.findRolesByUserId(1L)).thenReturn(List.of(userRole));

        assertFalse(userServiceAdapter.canCreateGames(1L));
    }
}
