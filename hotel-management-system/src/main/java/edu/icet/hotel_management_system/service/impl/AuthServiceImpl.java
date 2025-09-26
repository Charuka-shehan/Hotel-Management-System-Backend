package edu.icet.hotel_management_system.service.impl;

import edu.icet.hotel_management_system.exception.ResourceNotFoundException;
import edu.icet.hotel_management_system.model.dto.JwtAuthResponse;
import edu.icet.hotel_management_system.model.dto.LoginDto;
import edu.icet.hotel_management_system.model.dto.SignUpDto;
import edu.icet.hotel_management_system.model.dto.UserDto;
import edu.icet.hotel_management_system.model.entity.User;
import edu.icet.hotel_management_system.repository.UserRepository;
import edu.icet.hotel_management_system.security.CustomerUserDetailsService;
import edu.icet.hotel_management_system.security.JwtTokenProvider;
import edu.icet.hotel_management_system.service.AuthService;
import edu.icet.hotel_management_system.service.EmailService;
import edu.icet.hotel_management_system.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private EmailService emailService;

    @Autowired
    private CustomerUserDetailsService customUserDetailsService;

    @Override
    public JwtAuthResponse login(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = tokenProvider.generateToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(authentication);

        UserDto userDto = userService.getUserByEmail(loginDto.getEmail());
        return new JwtAuthResponse(accessToken, refreshToken, userDto);
    }

    @Override
    public String register(SignUpDto signUpDto) {
        if (userRepository.existsByEmail(signUpDto.getEmail())) {
            return "Email is already taken!";
        }

        UserDto userDto = modelMapper.map(signUpDto, UserDto.class);
        UserDto createdUser = userService.createUser(userDto);

        User user = userRepository.findByEmail(createdUser.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", createdUser.getEmail()));

        String verificationToken = UUID.randomUUID().toString();
        user.setVerificationToken(verificationToken);
        userRepository.save(user);

        emailService.sendVerificationEmail(user);

        return "User registered successfully. Please check your email for verification.";
    }

    @Override
    public String verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token);

        if (user == null) {
            return "Invalid verification token";
        }

        user.setEnabled(true);
        user.setVerificationToken(null);
        userRepository.save(user);

        return "Email verified successfully. You can now login.";
    }

    @Override
    public String forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        String resetToken = UUID.randomUUID().toString();
        user.setResetToken(resetToken);
        userRepository.save(user);

        emailService.sendPasswordResetEmail(user);

        return "Password reset instructions have been sent to your email.";
    }

    @Override
    public String resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token);

        if (user == null) {
            return "Invalid reset token";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        userRepository.save(user);

        return "Password reset successfully. You can now login with your new password.";
    }

    @Override
    public JwtAuthResponse oauth2Login(String email, String name) {
        User user;
        try {
            user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        } catch (ResourceNotFoundException e) {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setFirstName(name);
            newUser.setLastName("");
            newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
            newUser.setEnabled(true);

            user = userRepository.save(newUser);
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(), null, user.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = tokenProvider.generateToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(authentication);

        UserDto userDto = modelMapper.map(user, UserDto.class);
        return new JwtAuthResponse(accessToken, refreshToken, userDto);
    }

    @Override
    public JwtAuthResponse refreshToken(String refreshToken) {
        String username = tokenProvider.getUsernameFromJWT(refreshToken);
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        String newAccessToken = tokenProvider.generateToken(authentication);
        return new JwtAuthResponse(newAccessToken, refreshToken, modelMapper.map(userDetails, UserDto.class));
    }
}