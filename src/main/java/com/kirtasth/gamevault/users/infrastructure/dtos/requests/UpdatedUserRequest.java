package com.kirtasth.gamevault.users.infrastructure.dtos.requests;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UpdatedUserRequest {

    private String username;

    @Email
    private String email;

    private String password;

    private MultipartFile avatarImage;

    private String bio;

}
