package edu.icet.hotel_management_system.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class JwtAuthResponse {
    private String accessToken;
    private String refreshToken; // This was missing in one constructor
    private String tokenType = "Bearer";
    private UserDto user;

    public JwtAuthResponse(String accessToken, String refreshToken, UserDto user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.user = user;
        this.tokenType = "Bearer";
    }
}
