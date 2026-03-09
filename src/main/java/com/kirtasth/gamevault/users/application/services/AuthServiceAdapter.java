package com.kirtasth.gamevault.users.application.services;

import com.kirtasth.gamevault.common.domain.ports.out.ImageStoragePort;
import com.kirtasth.gamevault.users.domain.models.*;
import com.kirtasth.gamevault.users.domain.ports.in.AuthServicePort;
import com.kirtasth.gamevault.users.domain.ports.in.JwtServicePort;
import com.kirtasth.gamevault.users.domain.ports.in.UserServicePort;
import com.kirtasth.gamevault.users.domain.ports.out.AuthProviderPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceAdapter implements AuthServicePort {

    private final AuthProviderPort authProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserServicePort userService;
    private final JwtServicePort jwtService;
    private final ImageStoragePort userImageService;

    @Override
    public User registerUser(NewUser newUser) {
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));

        return this.userService.saveUser(newUser);
    }

    @Override
    public User updateUser(Long id, UpdatedUser updatedUser, MultipartFile avatarImage) {

        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isBlank()) {
            updatedUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        var avatarUrl = userImageService.uploadAvatar(avatarImage, id);

        updatedUser.setAvatarUrl(avatarUrl);

        return this.userService.updateUser(id, updatedUser);
    }

    @Override
    public AccessJwt login(Credentials credentials) {
        var email = credentials.getEmail();
        var password = credentials.getPassword();

        var authenticationToken = new UsernamePasswordAuthenticationToken(email, password, List.of());
        var authentication = authProvider.authenticate(authenticationToken);
        var authUser = (AuthUser) authentication.getPrincipal();

        this.jwtService.revokeAll(authUser.getId());

        return jwtService.getAccessJwt(authUser.getId(), authUser.getEmail(), authUser.getRoles());
    }

    @Override
    public AccessJwt refresh(RefreshTokenPetition refreshTokenPetition) {
        var refreshToken = refreshTokenPetition.getRefreshToken();
        var userId = jwtService.isNotExpiredAndNotRevoked(refreshToken).getUserId();

        jwtService.revokeAll(userId);

        var user = this.userService.getUserById(userId);

        return this.jwtService.getAccessJwt(userId, user.getEmail(), user.getRoles());
    }

    @Override
    public void logout(Long userId) {
        jwtService.revokeAll(userId);
    }
}
