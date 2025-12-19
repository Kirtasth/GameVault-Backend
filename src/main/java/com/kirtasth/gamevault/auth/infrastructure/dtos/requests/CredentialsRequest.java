package com.kirtasth.gamevault.auth.infrastructure.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CredentialsRequest {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
