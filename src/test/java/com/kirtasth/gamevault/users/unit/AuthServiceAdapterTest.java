package com.kirtasth.gamevault.users.unit;

import com.kirtasth.gamevault.common.domain.ports.out.ImageStoragePort;
import com.kirtasth.gamevault.users.application.services.AuthServiceAdapter;
import com.kirtasth.gamevault.users.domain.models.*;
import com.kirtasth.gamevault.users.domain.ports.in.JwtServicePort;
import com.kirtasth.gamevault.users.domain.ports.in.UserServicePort;
import com.kirtasth.gamevault.users.domain.ports.out.AuthProviderPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceAdapterTest {

    @Mock
    private AuthProviderPort authProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserServicePort userService;

    @Mock
    private JwtServicePort jwtService;

    @Mock
    private ImageStoragePort userImageService;

    @InjectMocks
    private AuthServiceAdapter authServiceAdapter;

    @Test
    void registerUser_ShouldEncodePasswordAndSave() {
        NewUser newUser = new NewUser();
        newUser.setPassword("rawPassword");
        
        User savedUser = new User();
        savedUser.setPassword("encodedPassword");

        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");
        when(userService.saveUser(newUser)).thenReturn(savedUser);

        User result = authServiceAdapter.registerUser(newUser);

        assertNotNull(result);
        assertEquals("encodedPassword", newUser.getPassword());
        verify(userService).saveUser(newUser);
    }

    @Test
    void login_ShouldAuthenticateAndReturnJwt() {
        Credentials credentials = new Credentials();
        credentials.setEmail("test@test.com");
        credentials.setPassword("password");

        AuthUser authUser = new AuthUser();
        authUser.setId(1L);
        authUser.setEmail("test@test.com");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(authUser);
        when(authProvider.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);

        AccessJwt expectedJwt = new AccessJwt(1L, "access", "refresh", "Bearer", 3600L, 7200L);
        when(jwtService.getAccessJwt(eq(1L), eq("test@test.com"), any())).thenReturn(expectedJwt);

        AccessJwt result = authServiceAdapter.login(credentials);

        assertEquals(expectedJwt, result);
        verify(jwtService).revokeAll(1L);
    }

    @Test
    void updateUser_ShouldUpdatePasswordAndAvatar() {
        UpdatedUser updatedUser = new UpdatedUser();
        updatedUser.setPassword("newPassword");
        MultipartFile avatar = mock(MultipartFile.class);

        when(passwordEncoder.encode("newPassword")).thenReturn("encodedPassword");
        when(userImageService.uploadAvatar(avatar, 1L)).thenReturn("http://avatar.url");
        
        User expectedUser = new User();
        when(userService.updateUser(eq(1L), eq(updatedUser))).thenReturn(expectedUser);

        User result = authServiceAdapter.updateUser(1L, updatedUser, avatar);

        assertEquals(expectedUser, result);
        assertEquals("encodedPassword", updatedUser.getPassword());
        assertEquals("http://avatar.url", updatedUser.getAvatarUrl());
        verify(userService).updateUser(1L, updatedUser);
    }

    @Test
    void logout_ShouldRevokeAllTokens() {
        authServiceAdapter.logout(1L);
        verify(jwtService).revokeAll(1L);
    }
}
