package edu.icet.hotel_management_system.controller;

import edu.icet.hotel_management_system.model.dto.JwtAuthResponse;
import edu.icet.hotel_management_system.model.dto.LoginDto;
import edu.icet.hotel_management_system.model.dto.SignUpDto;
import edu.icet.hotel_management_system.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth") // Fixed: Added /api prefix
@Tag(name = "Authentication", description = "Authentication APIs")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Operation(summary = "User login")
    @PostMapping("/signin")
    public ResponseEntity<JwtAuthResponse> authenticateUser(@Valid @RequestBody LoginDto loginDto) {
        JwtAuthResponse response = authService.login(loginDto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "User registration")
    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(@Valid @RequestBody SignUpDto signUpDto) {
        String response = authService.register(signUpDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Email verification")
    @GetMapping("/verify")
    public ResponseEntity<String> verifyUser(@RequestParam String token) {
        String response = authService.verifyEmail(token);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Forgot password")
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        String response = authService.forgotPassword(email);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Reset password")
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token,
                                                @RequestParam String newPassword) {
        String response = authService.resetPassword(token, newPassword);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "OAuth2 login callback")
    @PostMapping("/oauth2/callback")
    public ResponseEntity<JwtAuthResponse> oauth2Callback(@RequestParam String email,
                                                          @RequestParam String name) {
        JwtAuthResponse response = authService.oauth2Login(email, name);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Refresh access token")
    @PostMapping("/refresh-token")
    public ResponseEntity<JwtAuthResponse> refreshToken(@RequestParam String refreshToken) {
        JwtAuthResponse response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(response);
    }
}
