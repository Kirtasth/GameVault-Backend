package com.kirtasth.gamevault.checkout.infrastructure.dtos.requests;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Data
@Validated
public class UploadGameKeysRequest {
    @NotEmpty(message = "Keys list cannot be empty")
    private List<String> keys;
}
