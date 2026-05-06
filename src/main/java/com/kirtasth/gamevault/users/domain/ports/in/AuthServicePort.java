package com.kirtasth.gamevault.users.domain.ports.in;


import com.kirtasth.gamevault.users.domain.models.*;
import org.springframework.web.multipart.MultipartFile;

public interface AuthServicePort {

    AccessJwt login(Credentials credentials);

    User registerUser(NewUser newUser);

    User updateUser(Long id, UpdatedUser updatedUser, MultipartFile avatarImage);

    AccessJwt refresh(RefreshTokenPetition refreshTokenPetition);

    void logout(Long userId);
}
