package edu.icet.hotel_management_system.controller;

import edu.icet.hotel_management_system.model.entity.enums.ERole;
import edu.icet.hotel_management_system.service.RolePermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/roles-permissions")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Role & Permission Management", description = "APIs for managing roles and permissions")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RolePermissionController {

    @Autowired
    private RolePermissionService rolePermissionService;

    @Operation(summary = "Get all available permissions (Admin only)")
    @PreAuthorize("@permissionEvaluator.hasPermission(T(edu.icet.hotel_management_system.service.RolePermissionService.Permissions).MANAGE_SYSTEM)")
    @GetMapping("/permissions")
    public ResponseEntity<List<String>> getAllPermissions() {
        List<String> permissions = rolePermissionService.getAllAvailablePermissions();
        return ResponseEntity.ok(permissions);
    }

    @Operation(summary = "Get permissions for a specific role")
    @PreAuthorize("@permissionEvaluator.hasPermission(T(edu.icet.hotel_management_system.service.RolePermissionService.Permissions).VIEW_ALL_USERS)")
    @GetMapping("/permissions/{role}")
    public ResponseEntity<List<String>> getPermissionsByRole(@PathVariable String role) {
        try {
            ERole eRole = ERole.valueOf(role.toUpperCase());
            List<String> permissions = rolePermissionService.getUserPermissions(eRole);
            return ResponseEntity.ok(permissions);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + role);
        }
    }

    @Operation(summary = "Get categorized permissions for a role")
    @PreAuthorize("@permissionEvaluator.hasPermission(T(edu.icet.hotel_management_system.service.RolePermissionService.Permissions).VIEW_ALL_USERS)")
    @GetMapping("/permissions/{role}/categorized")
    public ResponseEntity<Map<String, List<String>>> getCategorizedPermissions(@PathVariable String role) {
        try {
            ERole eRole = ERole.valueOf(role.toUpperCase());
            Map<String, List<String>> categorizedPermissions = rolePermissionService.getPermissionsByCategory(eRole);
            return ResponseEntity.ok(categorizedPermissions);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + role);
        }
    }

    @Operation(summary = "Get permission summary for a role")
    @PreAuthorize("@permissionEvaluator.hasPermission(T(edu.icet.hotel_management_system.service.RolePermissionService.Permissions).VIEW_ALL_USERS)")
    @GetMapping("/summary/{role}")
    public ResponseEntity<Map<String, Object>> getPermissionSummary(@PathVariable String role) {
        try {
            ERole eRole = ERole.valueOf(role.toUpperCase());
            Map<String, Object> summary = rolePermissionService.getPermissionSummary(eRole);
            return ResponseEntity.ok(summary);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + role);
        }
    }

    @Operation(summary = "Check if role has specific permission")
    @PreAuthorize("@permissionEvaluator.hasPermission(T(edu.icet.hotel_management_system.service.RolePermissionService.Permissions).VIEW_ALL_USERS)")
    @GetMapping("/check/{role}/{permission}")
    public ResponseEntity<Map<String, Object>> checkPermission(
            @PathVariable String role,
            @PathVariable String permission) {
        try {
            ERole eRole = ERole.valueOf(role.toUpperCase());
            boolean hasPermission = rolePermissionService.hasPermission(eRole, permission);

            Map<String, Object> result = new HashMap<>();
            result.put("role", role);
            result.put("permission", permission);
            result.put("hasPermission", hasPermission);

            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + role);
        }
    }

    @Operation(summary = "Get all roles and their hierarchies")
    @PreAuthorize("@permissionEvaluator.hasPermission(T(edu.icet.hotel_management_system.service.RolePermissionService.Permissions).VIEW_ALL_USERS)")
    @GetMapping("/roles")
    public ResponseEntity<Map<String, Object>> getAllRoles() {
        Map<String, Object> rolesInfo = new HashMap<>();

        for (ERole role : ERole.values()) {
            Map<String, Object> roleData = new HashMap<>();
            roleData.put("name", role.name());
            roleData.put("level", rolePermissionService.getRoleLevel(role));
            roleData.put("permissionCount", rolePermissionService.getUserPermissions(role).size());
            roleData.put("canAssignRoles", rolePermissionService.getAssignableRoles(role));

            rolesInfo.put(role.name(), roleData);
        }

        return ResponseEntity.ok(rolesInfo);
    }

    @Operation(summary = "Compare permissions between two roles")
    @PreAuthorize("@permissionEvaluator.hasPermission(T(edu.icet.hotel_management_system.service.RolePermissionService.Permissions).VIEW_ALL_USERS)")
    @GetMapping("/compare/{role1}/{role2}")
    public ResponseEntity<Map<String, Object>> compareRoles(
            @PathVariable String role1,
            @PathVariable String role2) {
        try {
            ERole eRole1 = ERole.valueOf(role1.toUpperCase());
            ERole eRole2 = ERole.valueOf(role2.toUpperCase());

            List<String> permissions1 = rolePermissionService.getUserPermissions(eRole1);
            List<String> permissions2 = rolePermissionService.getUserPermissions(eRole2);

            Map<String, Object> comparison = new HashMap<>();
            comparison.put("role1", Map.of(
                    "name", role1,
                    "level", rolePermissionService.getRoleLevel(eRole1),
                    "permissions", permissions1,
                    "permissionCount", permissions1.size()
            ));
            comparison.put("role2", Map.of(
                    "name", role2,
                    "level", rolePermissionService.getRoleLevel(eRole2),
                    "permissions", permissions2,
                    "permissionCount", permissions2.size()
            ));
            comparison.put("higherPrivilegeRole",
                    rolePermissionService.hasHigherOrEqualPrivileges(eRole1, eRole2) ? role1 : role2);

            // Find common and unique permissions
            List<String> commonPermissions = permissions1.stream()
                    .filter(permissions2::contains)
                    .toList();

            List<String> uniqueToRole1 = permissions1.stream()
                    .filter(p -> !permissions2.contains(p))
                    .toList();

            List<String> uniqueToRole2 = permissions2.stream()
                    .filter(p -> !permissions1.contains(p))
                    .toList();

            comparison.put("commonPermissions", commonPermissions);
            comparison.put("uniqueToRole1", uniqueToRole1);
            comparison.put("uniqueToRole2", uniqueToRole2);

            return ResponseEntity.ok(comparison);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role provided");
        }
    }

    @Operation(summary = "Get roles that can be assigned by current user")
    @PreAuthorize("@permissionEvaluator.hasPermission(T(edu.icet.hotel_management_system.service.RolePermissionService.Permissions).ASSIGN_ROLES)")
    @GetMapping("/assignable/{role}")
    public ResponseEntity<List<ERole>> getAssignableRoles(@PathVariable String role) {
        try {
            ERole eRole = ERole.valueOf(role.toUpperCase());
            List<ERole> assignableRoles = rolePermissionService.getAssignableRoles(eRole);
            return ResponseEntity.ok(assignableRoles);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + role);
        }
    }

    @Operation(summary = "Validate if user can perform action (Admin only)")
    @PreAuthorize("@permissionEvaluator.hasPermission(T(edu.icet.hotel_management_system.service.RolePermissionService.Permissions).MANAGE_SYSTEM)")
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateUserAction(
            @RequestParam String userRole,
            @RequestParam String permission,
            @RequestParam(required = false) String targetRole) {

        try {
            ERole eUserRole = ERole.valueOf(userRole.toUpperCase());
            ERole eTargetRole = targetRole != null ? ERole.valueOf(targetRole.toUpperCase()) : null;

            boolean canPerform = rolePermissionService.canPerformAction(eUserRole, permission, eTargetRole);

            Map<String, Object> result = new HashMap<>();
            result.put("userRole", userRole);
            result.put("permission", permission);
            result.put("targetRole", targetRole);
            result.put("canPerformAction", canPerform);
            result.put("timestamp", java.time.LocalDateTime.now());

            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role provided");
        }
    }

    @Operation(summary = "Get permission matrix for all roles (Admin only)")
    @PreAuthorize("@permissionEvaluator.hasPermission(T(edu.icet.hotel_management_system.service.RolePermissionService.Permissions).MANAGE_SYSTEM)")
    @GetMapping("/matrix")
    public ResponseEntity<Map<String, Object>> getPermissionMatrix() {
        Map<String, Object> matrix = new HashMap<>();

        // Get all permissions
        List<String> allPermissions = rolePermissionService.getAllAvailablePermissions();
        matrix.put("permissions", allPermissions);

        // Get role-permission matrix
        Map<String, List<String>> rolePermissions = new HashMap<>();
        for (ERole role : ERole.values()) {
            rolePermissions.put(role.name(), rolePermissionService.getUserPermissions(role));
        }
        matrix.put("rolePermissions", rolePermissions);

        // Get role levels
        Map<String, Integer> roleLevels = new HashMap<>();
        for (ERole role : ERole.values()) {
            roleLevels.put(role.name(), rolePermissionService.getRoleLevel(role));
        }
        matrix.put("roleLevels", roleLevels);

        return ResponseEntity.ok(matrix);
    }

    @Operation(summary = "Get permission categories")
    @PreAuthorize("@permissionEvaluator.hasPermission(T(edu.icet.hotel_management_system.service.RolePermissionService.Permissions).VIEW_ALL_USERS)")
    @GetMapping("/categories")
    public ResponseEntity<Map<String, String>> getPermissionCategories() {
        Map<String, String> categories = new HashMap<>();
        categories.put("USER_MANAGEMENT", RolePermissionService.PermissionCategories.USER_MANAGEMENT);
        categories.put("ROOM_MANAGEMENT", RolePermissionService.PermissionCategories.ROOM_MANAGEMENT);
        categories.put("BOOKING_MANAGEMENT", RolePermissionService.PermissionCategories.BOOKING_MANAGEMENT);
        categories.put("PAYMENT_MANAGEMENT", RolePermissionService.PermissionCategories.PAYMENT_MANAGEMENT);
        categories.put("SYSTEM_MANAGEMENT", RolePermissionService.PermissionCategories.SYSTEM_MANAGEMENT);
        categories.put("REPORTS_ANALYTICS", RolePermissionService.PermissionCategories.REPORTS_ANALYTICS);
        categories.put("STAFF_MANAGEMENT", RolePermissionService.PermissionCategories.STAFF_MANAGEMENT);
        categories.put("CUSTOMER_SERVICE", RolePermissionService.PermissionCategories.CUSTOMER_SERVICE);

        return ResponseEntity.ok(categories);
    }

    @Operation(summary = "Get current user's permissions")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/my-permissions")
    public ResponseEntity<Map<String, Object>> getCurrentUserPermissions() {
        // This would need to get the current user's role from the security context
        // For now, return a placeholder response
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Implementation needed to get current user's role from security context");
        return ResponseEntity.ok(response);
    }
}
