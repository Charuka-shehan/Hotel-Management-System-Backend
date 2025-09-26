package edu.icet.hotel_management_system.service.impl;

import edu.icet.hotel_management_system.exception.ResourceNotFoundException;
import edu.icet.hotel_management_system.model.dto.UserDto;
import edu.icet.hotel_management_system.model.entity.User;
import edu.icet.hotel_management_system.model.entity.enums.ERole;
import edu.icet.hotel_management_system.repository.UserRepository;
import edu.icet.hotel_management_system.service.UserService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDto createUser(UserDto userDto) {
        logger.info("Creating user with email: {}", userDto.getEmail());

        try {
            // Check if user already exists
            if (userRepository.existsByEmail(userDto.getEmail())) {
                throw new IllegalArgumentException("User with email " + userDto.getEmail() + " already exists");
            }

            User user = modelMapper.map(userDto, User.class);

            // Encode password
            if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(userDto.getPassword()));
            } else {
                throw new IllegalArgumentException("Password is required");
            }

            // Enhanced role validation and assignment
            String role = userDto.getRole() != null ? userDto.getRole().toUpperCase() : "USER";
            try {
                ERole userRole = ERole.valueOf(role);
                user.setRole(userRole);

                // Additional validation for sensitive roles
                if (userRole == ERole.ADMIN || userRole == ERole.CASHIER || userRole == ERole.MANAGER) {
                    logger.info("Creating user with elevated role: {} for email: {}", userRole, userDto.getEmail());

                    // Get current authenticated user to check permissions
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                    if (authentication != null && authentication.isAuthenticated()) {
                        String currentUserEmail = authentication.getName();
                        User currentUser = userRepository.findByEmail(currentUserEmail).orElse(null);

                        // Only admins can create other admins, cashiers, or managers
                        if (currentUser != null && currentUser.getRole() != ERole.ADMIN) {
                            throw new IllegalArgumentException("Only administrators can create users with elevated roles");
                        }
                    }
                }

            } catch (IllegalArgumentException e) {
                logger.error("Invalid role provided: {}", role);
                throw new IllegalArgumentException("Invalid role: " + role +
                        ". Valid roles are: USER, ADMIN, CASHIER, MANAGER");
            }

            // Set additional user properties
            if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()) {
                throw new IllegalArgumentException("First name is required");
            }

            if (user.getLastName() == null || user.getLastName().trim().isEmpty()) {
                throw new IllegalArgumentException("Last name is required");
            }

            // Default to enabled for new users
            user.setEnabled(userDto.isEnabled());

            User savedUser = userRepository.save(user);
            logger.info("User created successfully with ID: {} and role: {}", savedUser.getId(), savedUser.getRole());

            UserDto responseDto = modelMapper.map(savedUser, UserDto.class);
            responseDto.setPassword(null); // Don't return password
            return responseDto;

        } catch (Exception e) {
            logger.error("Failed to create user: {}", e.getMessage());
            throw new RuntimeException("Failed to create user: " + e.getMessage());
        }
    }

    @Override
    public UserDto getUserById(Long id) {
        logger.info("Fetching user with id: {}", id);

        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

            UserDto userDto = modelMapper.map(user, UserDto.class);
            userDto.setPassword(null); // Don't return password
            return userDto;
        } catch (Exception e) {
            logger.error("Failed to fetch user by ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Failed to fetch user: " + e.getMessage());
        }
    }

    @Override
    public UserDto getUserByEmail(String email) {
        logger.info("Fetching user with email: {}", email);

        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

            UserDto userDto = modelMapper.map(user, UserDto.class);
            userDto.setPassword(null); // Don't return password
            return userDto;
        } catch (Exception e) {
            logger.error("Failed to fetch user by email {}: {}", email, e.getMessage());
            throw new RuntimeException("Failed to fetch user: " + e.getMessage());
        }
    }

    @Override
    public List<UserDto> getAllUsers() {
        logger.info("Fetching all users");

        try {
            List<User> users = userRepository.findAll();
            return users.stream()
                    .map(user -> {
                        UserDto dto = modelMapper.map(user, UserDto.class);
                        dto.setPassword(null); // Don't return passwords
                        return dto;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Failed to fetch all users: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch users: " + e.getMessage());
        }
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        logger.info("Updating user with id: {}", id);

        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

            // Check if current user has permission to update this user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                String currentUserEmail = authentication.getName();
                User currentUser = userRepository.findByEmail(currentUserEmail).orElse(null);

                // Users can only update their own profile unless they're admin
                if (currentUser != null && currentUser.getRole() != ERole.ADMIN &&
                        !currentUser.getId().equals(id)) {
                    throw new IllegalArgumentException("You can only update your own profile");
                }
            }

            // Update basic fields
            if (userDto.getFirstName() != null && !userDto.getFirstName().trim().isEmpty()) {
                user.setFirstName(userDto.getFirstName().trim());
            }

            if (userDto.getLastName() != null && !userDto.getLastName().trim().isEmpty()) {
                user.setLastName(userDto.getLastName().trim());
            }

            // Email update with validation
            if (userDto.getEmail() != null && !userDto.getEmail().equals(user.getEmail())) {
                if (userRepository.existsByEmail(userDto.getEmail())) {
                    throw new IllegalArgumentException("Email already exists: " + userDto.getEmail());
                }
                user.setEmail(userDto.getEmail());
            }

            if (userDto.getPhone() != null) {
                user.setPhone(userDto.getPhone());
            }

            if (userDto.getAddress() != null) {
                user.setAddress(userDto.getAddress());
            }

            // Password update
            if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(userDto.getPassword()));
                logger.info("Password updated for user: {}", user.getEmail());
            }

            // Role update (admin only)
            if (userDto.getRole() != null) {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.isAuthenticated()) {
                    String currentUserEmail = auth.getName();
                    User currentUser = userRepository.findByEmail(currentUserEmail).orElse(null);

                    if (currentUser == null || currentUser.getRole() != ERole.ADMIN) {
                        throw new IllegalArgumentException("Only administrators can change user roles");
                    }
                }

                try {
                    ERole newRole = ERole.valueOf(userDto.getRole().toUpperCase());
                    user.setRole(newRole);
                    logger.info("Role updated to {} for user: {}", newRole, user.getEmail());
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Invalid role: " + userDto.getRole());
                }
            }

            // Update enabled status (admin only)
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) {
                String currentUserEmail = auth.getName();
                User currentUser = userRepository.findByEmail(currentUserEmail).orElse(null);

                if (currentUser != null && currentUser.getRole() == ERole.ADMIN) {
                    user.setEnabled(userDto.isEnabled());
                }
            }

            User updatedUser = userRepository.save(user);
            logger.info("User updated successfully: {}", updatedUser.getEmail());

            UserDto responseDto = modelMapper.map(updatedUser, UserDto.class);
            responseDto.setPassword(null);
            return responseDto;

        } catch (Exception e) {
            logger.error("Failed to update user {}: {}", id, e.getMessage());
            throw new RuntimeException("Failed to update user: " + e.getMessage());
        }
    }

    @Override
    public void deleteUser(Long id) {
        logger.info("Deleting user with id: {}", id);

        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

            // Check permissions - only admin can delete users
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                String currentUserEmail = authentication.getName();
                User currentUser = userRepository.findByEmail(currentUserEmail).orElse(null);

                if (currentUser == null || currentUser.getRole() != ERole.ADMIN) {
                    throw new IllegalArgumentException("Only administrators can delete users");
                }

                // Prevent admin from deleting themselves
                if (currentUser.getId().equals(id)) {
                    throw new IllegalArgumentException("You cannot delete your own account");
                }
            }

            logger.info("Deleting user: {} with role: {}", user.getEmail(), user.getRole());
            userRepository.delete(user);

        } catch (Exception e) {
            logger.error("Failed to delete user {}: {}", id, e.getMessage());
            throw new RuntimeException("Failed to delete user: " + e.getMessage());
        }
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        logger.info("Attempting to change password for authenticated user");

        try {
            // Get the currently authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                throw new IllegalArgumentException("User not authenticated");
            }

            String email = authentication.getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

            // Verify old password
            if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                logger.error("Old password is incorrect for user: {}", email);
                throw new IllegalArgumentException("Current password is incorrect");
            }

            // Validate new password
            if (newPassword == null || newPassword.trim().length() < 6) {
                throw new IllegalArgumentException("New password must be at least 6 characters long");
            }

            // Encode and set new password
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);

            logger.info("Password changed successfully for user: {}", email);

        } catch (Exception e) {
            logger.error("Failed to change password: {}", e.getMessage());
            throw new RuntimeException("Failed to change password: " + e.getMessage());
        }
    }
}