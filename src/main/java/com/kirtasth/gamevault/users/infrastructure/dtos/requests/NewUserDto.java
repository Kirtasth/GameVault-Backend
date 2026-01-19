package com.kirtasth.gamevault.users.infrastructure.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
public class NewUserDto {

    @NotBlank
    private String username;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    @URL
    private String avatarUrl;

    private String bio;

    @NotBlank
    private String loginProvider;

    @NotBlank
    private String loginProvidedUserId;
}
