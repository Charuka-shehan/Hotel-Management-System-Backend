package edu.icet.hotel_management_system.model.dto;

import lombok.Data;

import java.util.Set;

@Data
public class SignUpDto {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phone;
    private String address;
    private Set<String> roles; // Set of role names (e.g., ["USER"])
}