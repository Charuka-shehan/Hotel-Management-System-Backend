package edu.icet.hotel_management_system.controller;

import edu.icet.hotel_management_system.model.dto.ChangePasswordDto;
import edu.icet.hotel_management_system.model.dto.UserDto;
import edu.icet.hotel_management_system.service.RolePermissionService;
import edu.icet.hotel_management_system.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@SecurityRequirement(name = "bearerAuth")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "Create user (Admin only)")
    @PreAuthorize("@permissionEvaluator.hasPermission(T(edu.icet.hotel_management_system.service.RolePermissionService.Permissions).CREATE_USER)")
    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {
        UserDto createdUser = userService.createUser(userDto);
        return ResponseEntity.ok(createdUser);
    }

    @Operation(summary = "Get user by ID")
    @PreAuthorize("@permissionEvaluator.hasPermission(T(edu.icet.hotel_management_system.service.RolePermissionService.Permissions).VIEW_ALL_USERS) or " +
            "@permissionEvaluator.canAccessUserResource(#id)")
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        UserDto user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Get user by email (Admin only)")
    @PreAuthorize("@permissionEvaluator.hasPermission(T(edu.icet.hotel_management_system.service.RolePermissionService.Permissions).VIEW_ALL_USERS)")
    @GetMapping("/email/{email}")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable String email) {
        UserDto user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Get all users (Admin only)")
    @PreAuthorize("@permissionEvaluator.hasPermission(T(edu.icet.hotel_management_system.service.RolePermissionService.Permissions).VIEW_ALL_USERS)")
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Update user")
    @PreAuthorize("@permissionEvaluator.hasPermission(T(edu.icet.hotel_management_system.service.RolePermissionService.Permissions).UPDATE_USER) or " +
            "@permissionEvaluator.canAccessUserResource(#id)")
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        UserDto updatedUser = userService.updateUser(id, userDto);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "Delete user (Admin only)")
    @PreAuthorize("@permissionEvaluator.hasPermission(T(edu.icet.hotel_management_system.service.RolePermissionService.Permissions).DELETE_USER)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Change password")
    @PreAuthorize("@permissionEvaluator.hasPermission(T(edu.icet.hotel_management_system.service.RolePermissionService.Permissions).CHANGE_PASSWORD)")
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordDto changePasswordDto) {
        userService.changePassword(changePasswordDto.getOldPassword(), changePasswordDto.getNewPassword());
        return ResponseEntity.ok("Password changed successfully");
    }
}
