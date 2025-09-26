package edu.icet.hotel_management_system.model.dto;

import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phone;
    private String address;
    private String role; // String to match ERole enum values (e.g., "USER", "ADMIN")
    private boolean enabled;
    private String verificationToken;
    private String resetToken;
}