package com.kirtasth.gamevault.users.infrastructure.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshTokenPetitionRequest {

    @NotBlank(message = "Refresh token is required")
    private String refreshToken;

}
