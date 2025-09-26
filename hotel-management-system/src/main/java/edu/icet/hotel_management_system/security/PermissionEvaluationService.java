package edu.icet.hotel_management_system.security;

import edu.icet.hotel_management_system.model.entity.User;
import edu.icet.hotel_management_system.model.entity.enums.ERole;
import edu.icet.hotel_management_system.repository.UserRepository;
import edu.icet.hotel_management_system.service.RolePermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service("permissionEvaluator")
public class PermissionEvaluationService {

    @Autowired
    private RolePermissionService rolePermissionService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Check if current user has specific permission
     */
    public boolean hasPermission(String permission) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }

        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return false;
        }

        return rolePermissionService.hasPermission(user.getRole(), permission);
    }

    /**
     * Check if current user has any of the specified permissions
     */
    public boolean hasAnyPermission(String... permissions) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }

        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return false;
        }

        return rolePermissionService.hasAnyPermission(user.getRole(), permissions);
    }

    /**
     * Check if current user can access resource owned by specific user
     */
    public boolean canAccessUserResource(Long userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }

        String email = auth.getName();
        User currentUser = userRepository.findByEmail(email).orElse(null);

        if (currentUser == null) {
            return false;
        }

        // Admin can access all resources
        if (currentUser.getRole() == ERole.ADMIN) {
            return true;
        }

        // Users can only access their own resources
        return currentUser.getId().equals(userId);
    }

    /**
     * Check if current user can access booking
     */
    public boolean canAccessBooking(Long bookingId) {
        // Implementation would check if booking belongs to current user or if user has admin/manager role
        // This is simplified - you'd need to inject BookingRepository and check ownership
        return hasPermission(RolePermissionService.Permissions.VIEW_ALL_BOOKINGS) ||
                hasPermission(RolePermissionService.Permissions.VIEW_OWN_BOOKINGS);
    }

    /**
     * Check if current user can access payment
     */
    public boolean canAccessPayment(Long paymentId) {
        // Implementation would check if payment belongs to current user's booking or if user has admin/cashier role
        return hasPermission(RolePermissionService.Permissions.VIEW_ALL_PAYMENTS) ||
                hasPermission(RolePermissionService.Permissions.VIEW_OWN_PAYMENTS);
    }
}
