package edu.icet.hotel_management_system.model.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class LoginDto {
    @NotBlank
    private String email;

    @NotBlank
    private String password;
}