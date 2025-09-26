package edu.icet.hotel_management_system.service;

import edu.icet.hotel_management_system.model.dto.JwtAuthResponse;
import edu.icet.hotel_management_system.model.dto.LoginDto;
import edu.icet.hotel_management_system.model.dto.SignUpDto;

public interface AuthService {

    JwtAuthResponse login(LoginDto loginDto);

    String register(SignUpDto signUpDto);

    String verifyEmail(String token);

    String forgotPassword(String email);

    String resetPassword(String token, String newPassword);

    JwtAuthResponse oauth2Login(String email, String name);

    JwtAuthResponse refreshToken(String refreshToken);
}