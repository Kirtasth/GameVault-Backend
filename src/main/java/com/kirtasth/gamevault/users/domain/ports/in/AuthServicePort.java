package com.kirtasth.gamevault.users.domain.ports.in;


import com.kirtasth.gamevault.users.domain.models.AccessJwt;
import com.kirtasth.gamevault.users.domain.models.Credentials;
import com.kirtasth.gamevault.users.domain.models.NewUser;
import com.kirtasth.gamevault.users.domain.models.RefreshTokenPetition;
import com.kirtasth.gamevault.users.domain.models.UpdatedUser;
import com.kirtasth.gamevault.users.domain.models.User;
import org.springframework.web.multipart.MultipartFile;

public interface AuthServicePort {

    AccessJwt login(Credentials credentials);

    User registerUser(NewUser newUser);

    User updateUser(Long id, UpdatedUser updatedUser, MultipartFile avatarImage);

    AccessJwt refresh(RefreshTokenPetition refreshTokenPetition);

    void logout(Long userId);
}
