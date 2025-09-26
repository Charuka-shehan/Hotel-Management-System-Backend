package edu.icet.hotel_management_system.service;

import edu.icet.hotel_management_system.model.entity.enums.ERole;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RolePermissionService {

    // Define all available permissions
    public static final class Permissions {
        // User Management
        public static final String VIEW_ALL_USERS = "VIEW_ALL_USERS";
        public static final String CREATE_USER = "CREATE_USER";
        public static final String UPDATE_USER = "UPDATE_USER";
        public static final String DELETE_USER = "DELETE_USER";
        public static final String VIEW_USER_PROFILE = "VIEW_USER_PROFILE";
        public static final String UPDATE_USER_PROFILE = "UPDATE_USER_PROFILE";
        public static final String CHANGE_PASSWORD = "CHANGE_PASSWORD";
        public static final String RESET_USER_PASSWORD = "RESET_USER_PASSWORD";

        // Room Management
        public static final String VIEW_ALL_ROOMS = "VIEW_ALL_ROOMS";
        public static final String CREATE_ROOM = "CREATE_ROOM";
        public static final String UPDATE_ROOM = "UPDATE_ROOM";
        public static final String DELETE_ROOM = "DELETE_ROOM";
        public static final String UPLOAD_ROOM_IMAGE = "UPLOAD_ROOM_IMAGE";
        public static final String DELETE_ROOM_IMAGE = "DELETE_ROOM_IMAGE";
        public static final String VIEW_ROOM_AVAILABILITY = "VIEW_ROOM_AVAILABILITY";
        public static final String SEARCH_ROOMS = "SEARCH_ROOMS";

        // Booking Management
        public static final String VIEW_ALL_BOOKINGS = "VIEW_ALL_BOOKINGS";
        public static final String VIEW_OWN_BOOKINGS = "VIEW_OWN_BOOKINGS";
        public static final String CREATE_BOOKING = "CREATE_BOOKING";
        public static final String UPDATE_BOOKING = "UPDATE_BOOKING";
        public static final String DELETE_BOOKING = "DELETE_BOOKING";
        public static final String CONFIRM_BOOKING = "CONFIRM_BOOKING";
        public static final String CANCEL_BOOKING = "CANCEL_BOOKING";
        public static final String COMPLETE_BOOKING = "COMPLETE_BOOKING";
        public static final String SEARCH_BOOKINGS = "SEARCH_BOOKINGS";

        // Payment Management
        public static final String VIEW_ALL_PAYMENTS = "VIEW_ALL_PAYMENTS";
        public static final String VIEW_OWN_PAYMENTS = "VIEW_OWN_PAYMENTS";
        public static final String PROCESS_ONLINE_PAYMENTS = "PROCESS_ONLINE_PAYMENTS";
        public static final String PROCESS_CASH_PAYMENTS = "PROCESS_CASH_PAYMENTS";
        public static final String PROCESS_CARD_PAYMENTS = "PROCESS_CARD_PAYMENTS";
        public static final String PROCESS_BANK_TRANSFERS = "PROCESS_BANK_TRANSFERS";
        public static final String PROCESS_MOBILE_PAYMENTS = "PROCESS_MOBILE_PAYMENTS";
        public static final String REFUND_PAYMENTS = "REFUND_PAYMENTS";
        public static final String UPDATE_PAYMENT_STATUS = "UPDATE_PAYMENT_STATUS";
        public static final String GENERATE_RECEIPTS = "GENERATE_RECEIPTS";
        public static final String VIEW_PAYMENT_STATISTICS = "VIEW_PAYMENT_STATISTICS";

        // System Management
        public static final String MANAGE_SYSTEM = "MANAGE_SYSTEM";
        public static final String VIEW_SYSTEM_LOGS = "VIEW_SYSTEM_LOGS";
        public static final String BACKUP_RESTORE = "BACKUP_RESTORE";
        public static final String MANAGE_SETTINGS = "MANAGE_SETTINGS";

        // Reports and Analytics
        public static final String VIEW_REPORTS = "VIEW_REPORTS";
        public static final String GENERATE_REPORTS = "GENERATE_REPORTS";
        public static final String VIEW_ANALYTICS = "VIEW_ANALYTICS";
        public static final String EXPORT_DATA = "EXPORT_DATA";

        // Staff Management
        public static final String MANAGE_STAFF = "MANAGE_STAFF";
        public static final String ASSIGN_ROLES = "ASSIGN_ROLES";
        public static final String VIEW_STAFF_PERFORMANCE = "VIEW_STAFF_PERFORMANCE";

        // Customer Service
        public static final String HANDLE_CUSTOMER_QUERIES = "HANDLE_CUSTOMER_QUERIES";
        public static final String PROCESS_COMPLAINTS = "PROCESS_COMPLAINTS";
        public static final String SEND_NOTIFICATIONS = "SEND_NOTIFICATIONS";
    }

    // Permission categories for better organization
    public static final class PermissionCategories {
        public static final String USER_MANAGEMENT = "User Management";
        public static final String ROOM_MANAGEMENT = "Room Management";
        public static final String BOOKING_MANAGEMENT = "Booking Management";
        public static final String PAYMENT_MANAGEMENT = "Payment Management";
        public static final String SYSTEM_MANAGEMENT = "System Management";
        public static final String REPORTS_ANALYTICS = "Reports & Analytics";
        public static final String STAFF_MANAGEMENT = "Staff Management";
        public static final String CUSTOMER_SERVICE = "Customer Service";
    }

    /**
     * Get all permissions for a specific role
     */
    public List<String> getUserPermissions(ERole role) {
        switch (role) {
            case ADMIN:
                return getAdminPermissions();
            case MANAGER:
                return getManagerPermissions();
            case CASHIER:
                return getCashierPermissions();
            case USER:
            default:
                return getUserPermissions();
        }
    }

    /**
     * Admin permissions - Full system access
     */
    private List<String> getAdminPermissions() {
        return Arrays.asList(
                // User Management - Full access
                Permissions.VIEW_ALL_USERS,
                Permissions.CREATE_USER,
                Permissions.UPDATE_USER,
                Permissions.DELETE_USER,
                Permissions.VIEW_USER_PROFILE,
                Permissions.UPDATE_USER_PROFILE,
                Permissions.CHANGE_PASSWORD,
                Permissions.RESET_USER_PASSWORD,

                // Room Management - Full access
                Permissions.VIEW_ALL_ROOMS,
                Permissions.CREATE_ROOM,
                Permissions.UPDATE_ROOM,
                Permissions.DELETE_ROOM,
                Permissions.UPLOAD_ROOM_IMAGE,
                Permissions.DELETE_ROOM_IMAGE,
                Permissions.VIEW_ROOM_AVAILABILITY,
                Permissions.SEARCH_ROOMS,

                // Booking Management - Full access
                Permissions.VIEW_ALL_BOOKINGS,
                Permissions.VIEW_OWN_BOOKINGS,
                Permissions.CREATE_BOOKING,
                Permissions.UPDATE_BOOKING,
                Permissions.DELETE_BOOKING,
                Permissions.CONFIRM_BOOKING,
                Permissions.CANCEL_BOOKING,
                Permissions.COMPLETE_BOOKING,
                Permissions.SEARCH_BOOKINGS,

                // Payment Management - Full access
                Permissions.VIEW_ALL_PAYMENTS,
                Permissions.VIEW_OWN_PAYMENTS,
                Permissions.PROCESS_ONLINE_PAYMENTS,
                Permissions.PROCESS_CASH_PAYMENTS,
                Permissions.PROCESS_CARD_PAYMENTS,
                Permissions.PROCESS_BANK_TRANSFERS,
                Permissions.PROCESS_MOBILE_PAYMENTS,
                Permissions.REFUND_PAYMENTS,
                Permissions.UPDATE_PAYMENT_STATUS,
                Permissions.GENERATE_RECEIPTS,
                Permissions.VIEW_PAYMENT_STATISTICS,

                // System Management - Full access
                Permissions.MANAGE_SYSTEM,
                Permissions.VIEW_SYSTEM_LOGS,
                Permissions.BACKUP_RESTORE,
                Permissions.MANAGE_SETTINGS,

                // Reports and Analytics - Full access
                Permissions.VIEW_REPORTS,
                Permissions.GENERATE_REPORTS,
                Permissions.VIEW_ANALYTICS,
                Permissions.EXPORT_DATA,

                // Staff Management - Full access
                Permissions.MANAGE_STAFF,
                Permissions.ASSIGN_ROLES,
                Permissions.VIEW_STAFF_PERFORMANCE,

                // Customer Service - Full access
                Permissions.HANDLE_CUSTOMER_QUERIES,
                Permissions.PROCESS_COMPLAINTS,
                Permissions.SEND_NOTIFICATIONS
        );
    }

    /**
     * Manager permissions - High level operational access
     */
    private List<String> getManagerPermissions() {
        return Arrays.asList(
                // User Management - Limited
                Permissions.VIEW_ALL_USERS,
                Permissions.VIEW_USER_PROFILE,
                Permissions.UPDATE_USER_PROFILE,
                Permissions.CHANGE_PASSWORD,

                // Room Management - View and Update only
                Permissions.VIEW_ALL_ROOMS,
                Permissions.UPDATE_ROOM,
                Permissions.VIEW_ROOM_AVAILABILITY,
                Permissions.SEARCH_ROOMS,

                // Booking Management - Full operational access
                Permissions.VIEW_ALL_BOOKINGS,
                Permissions.VIEW_OWN_BOOKINGS,
                Permissions.CREATE_BOOKING,
                Permissions.UPDATE_BOOKING,
                Permissions.CONFIRM_BOOKING,
                Permissions.CANCEL_BOOKING,
                Permissions.COMPLETE_BOOKING,
                Permissions.SEARCH_BOOKINGS,

                // Payment Management - Process and refund payments
                Permissions.VIEW_ALL_PAYMENTS,
                Permissions.VIEW_OWN_PAYMENTS,
                Permissions.PROCESS_ONLINE_PAYMENTS,
                Permissions.PROCESS_CASH_PAYMENTS,
                Permissions.PROCESS_CARD_PAYMENTS,
                Permissions.PROCESS_BANK_TRANSFERS,
                Permissions.PROCESS_MOBILE_PAYMENTS,
                Permissions.REFUND_PAYMENTS,
                Permissions.GENERATE_RECEIPTS,
                Permissions.VIEW_PAYMENT_STATISTICS,

                // Reports and Analytics - View access
                Permissions.VIEW_REPORTS,
                Permissions.GENERATE_REPORTS,
                Permissions.VIEW_ANALYTICS,
                Permissions.EXPORT_DATA,

                // Staff Management - Limited
                Permissions.MANAGE_STAFF,
                Permissions.VIEW_STAFF_PERFORMANCE,

                // Customer Service - Full access
                Permissions.HANDLE_CUSTOMER_QUERIES,
                Permissions.PROCESS_COMPLAINTS,
                Permissions.SEND_NOTIFICATIONS
        );
    }

    /**
     * Cashier permissions - Payment and receipt focused
     */
    private List<String> getCashierPermissions() {
        return Arrays.asList(
                // User Management - Very limited
                Permissions.VIEW_USER_PROFILE,
                Permissions.UPDATE_USER_PROFILE,
                Permissions.CHANGE_PASSWORD,

                // Room Management - View only
                Permissions.VIEW_ALL_ROOMS,
                Permissions.VIEW_ROOM_AVAILABILITY,
                Permissions.SEARCH_ROOMS,

                // Booking Management - Limited to payment-related
                Permissions.VIEW_ALL_BOOKINGS,
                Permissions.SEARCH_BOOKINGS,

                // Payment Management - Core cashier functions
                Permissions.VIEW_ALL_PAYMENTS,
                Permissions.PROCESS_CASH_PAYMENTS,
                Permissions.PROCESS_CARD_PAYMENTS,
                Permissions.PROCESS_MOBILE_PAYMENTS,
                Permissions.GENERATE_RECEIPTS,
                Permissions.VIEW_PAYMENT_STATISTICS,

                // Customer Service - Basic support
                Permissions.HANDLE_CUSTOMER_QUERIES,
                Permissions.GENERATE_RECEIPTS
        );
    }

    /**
     * Regular user permissions - Self-service only
     */
    private List<String> getUserPermissions() {
        return Arrays.asList(
                // User Management - Own profile only
                Permissions.VIEW_USER_PROFILE,
                Permissions.UPDATE_USER_PROFILE,
                Permissions.CHANGE_PASSWORD,

                // Room Management - Browse and search
                Permissions.VIEW_ALL_ROOMS,
                Permissions.VIEW_ROOM_AVAILABILITY,
                Permissions.SEARCH_ROOMS,

                // Booking Management - Own bookings
                Permissions.VIEW_OWN_BOOKINGS,
                Permissions.CREATE_BOOKING,

                // Payment Management - Own payments
                Permissions.VIEW_OWN_PAYMENTS,
                Permissions.PROCESS_ONLINE_PAYMENTS,
                Permissions.PROCESS_CARD_PAYMENTS,
                Permissions.PROCESS_BANK_TRANSFERS,
                Permissions.PROCESS_MOBILE_PAYMENTS
        );
    }

    /**
     * Check if a role has a specific permission
     */
    public boolean hasPermission(ERole role, String permission) {
        return getUserPermissions(role).contains(permission);
    }

    /**
     * Check if a role has any of the specified permissions
     */
    public boolean hasAnyPermission(ERole role, String... permissions) {
        List<String> userPermissions = getUserPermissions(role);
        return Arrays.stream(permissions)
                .anyMatch(userPermissions::contains);
    }

    /**
     * Check if a role has all of the specified permissions
     */
    public boolean hasAllPermissions(ERole role, String... permissions) {
        List<String> userPermissions = getUserPermissions(role);
        return Arrays.stream(permissions)
                .allMatch(userPermissions::contains);
    }

    /**
     * Get permissions by category for a specific role
     */
    public Map<String, List<String>> getPermissionsByCategory(ERole role) {
        List<String> userPermissions = getUserPermissions(role);
        Map<String, List<String>> categorizedPermissions = new HashMap<>();

        // User Management
        categorizedPermissions.put(PermissionCategories.USER_MANAGEMENT,
                filterPermissionsByCategory(userPermissions,
                        Arrays.asList(
                                Permissions.VIEW_ALL_USERS, Permissions.CREATE_USER,
                                Permissions.UPDATE_USER, Permissions.DELETE_USER,
                                Permissions.VIEW_USER_PROFILE, Permissions.UPDATE_USER_PROFILE,
                                Permissions.CHANGE_PASSWORD, Permissions.RESET_USER_PASSWORD
                        )));

        // Room Management
        categorizedPermissions.put(PermissionCategories.ROOM_MANAGEMENT,
                filterPermissionsByCategory(userPermissions,
                        Arrays.asList(
                                Permissions.VIEW_ALL_ROOMS, Permissions.CREATE_ROOM,
                                Permissions.UPDATE_ROOM, Permissions.DELETE_ROOM,
                                Permissions.UPLOAD_ROOM_IMAGE, Permissions.DELETE_ROOM_IMAGE,
                                Permissions.VIEW_ROOM_AVAILABILITY, Permissions.SEARCH_ROOMS
                        )));

        // Booking Management
        categorizedPermissions.put(PermissionCategories.BOOKING_MANAGEMENT,
                filterPermissionsByCategory(userPermissions,
                        Arrays.asList(
                                Permissions.VIEW_ALL_BOOKINGS, Permissions.VIEW_OWN_BOOKINGS,
                                Permissions.CREATE_BOOKING, Permissions.UPDATE_BOOKING,
                                Permissions.DELETE_BOOKING, Permissions.CONFIRM_BOOKING,
                                Permissions.CANCEL_BOOKING, Permissions.COMPLETE_BOOKING,
                                Permissions.SEARCH_BOOKINGS
                        )));

        // Payment Management
        categorizedPermissions.put(PermissionCategories.PAYMENT_MANAGEMENT,
                filterPermissionsByCategory(userPermissions,
                        Arrays.asList(
                                Permissions.VIEW_ALL_PAYMENTS, Permissions.VIEW_OWN_PAYMENTS,
                                Permissions.PROCESS_ONLINE_PAYMENTS, Permissions.PROCESS_CASH_PAYMENTS,
                                Permissions.PROCESS_CARD_PAYMENTS, Permissions.PROCESS_BANK_TRANSFERS,
                                Permissions.PROCESS_MOBILE_PAYMENTS, Permissions.REFUND_PAYMENTS,
                                Permissions.UPDATE_PAYMENT_STATUS, Permissions.GENERATE_RECEIPTS,
                                Permissions.VIEW_PAYMENT_STATISTICS
                        )));

        // System Management
        categorizedPermissions.put(PermissionCategories.SYSTEM_MANAGEMENT,
                filterPermissionsByCategory(userPermissions,
                        Arrays.asList(
                                Permissions.MANAGE_SYSTEM, Permissions.VIEW_SYSTEM_LOGS,
                                Permissions.BACKUP_RESTORE, Permissions.MANAGE_SETTINGS
                        )));

        // Reports & Analytics
        categorizedPermissions.put(PermissionCategories.REPORTS_ANALYTICS,
                filterPermissionsByCategory(userPermissions,
                        Arrays.asList(
                                Permissions.VIEW_REPORTS, Permissions.GENERATE_REPORTS,
                                Permissions.VIEW_ANALYTICS, Permissions.EXPORT_DATA
                        )));

        // Staff Management
        categorizedPermissions.put(PermissionCategories.STAFF_MANAGEMENT,
                filterPermissionsByCategory(userPermissions,
                        Arrays.asList(
                                Permissions.MANAGE_STAFF, Permissions.ASSIGN_ROLES,
                                Permissions.VIEW_STAFF_PERFORMANCE
                        )));

        // Customer Service
        categorizedPermissions.put(PermissionCategories.CUSTOMER_SERVICE,
                filterPermissionsByCategory(userPermissions,
                        Arrays.asList(
                                Permissions.HANDLE_CUSTOMER_QUERIES, Permissions.PROCESS_COMPLAINTS,
                                Permissions.SEND_NOTIFICATIONS
                        )));

        return categorizedPermissions;
    }

    /**
     * Helper method to filter permissions by category
     */
    private List<String> filterPermissionsByCategory(List<String> userPermissions, List<String> categoryPermissions) {
        return categoryPermissions.stream()
                .filter(userPermissions::contains)
                .collect(Collectors.toList());
    }

    /**
     * Get all available permissions in the system
     */
    public List<String> getAllAvailablePermissions() {
        return Arrays.asList(
                // User Management
                Permissions.VIEW_ALL_USERS, Permissions.CREATE_USER, Permissions.UPDATE_USER,
                Permissions.DELETE_USER, Permissions.VIEW_USER_PROFILE, Permissions.UPDATE_USER_PROFILE,
                Permissions.CHANGE_PASSWORD, Permissions.RESET_USER_PASSWORD,

                // Room Management
                Permissions.VIEW_ALL_ROOMS, Permissions.CREATE_ROOM, Permissions.UPDATE_ROOM,
                Permissions.DELETE_ROOM, Permissions.UPLOAD_ROOM_IMAGE, Permissions.DELETE_ROOM_IMAGE,
                Permissions.VIEW_ROOM_AVAILABILITY, Permissions.SEARCH_ROOMS,

                // Booking Management
                Permissions.VIEW_ALL_BOOKINGS, Permissions.VIEW_OWN_BOOKINGS, Permissions.CREATE_BOOKING,
                Permissions.UPDATE_BOOKING, Permissions.DELETE_BOOKING, Permissions.CONFIRM_BOOKING,
                Permissions.CANCEL_BOOKING, Permissions.COMPLETE_BOOKING, Permissions.SEARCH_BOOKINGS,

                // Payment Management
                Permissions.VIEW_ALL_PAYMENTS, Permissions.VIEW_OWN_PAYMENTS, Permissions.PROCESS_ONLINE_PAYMENTS,
                Permissions.PROCESS_CASH_PAYMENTS, Permissions.PROCESS_CARD_PAYMENTS, Permissions.PROCESS_BANK_TRANSFERS,
                Permissions.PROCESS_MOBILE_PAYMENTS, Permissions.REFUND_PAYMENTS, Permissions.UPDATE_PAYMENT_STATUS,
                Permissions.GENERATE_RECEIPTS, Permissions.VIEW_PAYMENT_STATISTICS,

                // System Management
                Permissions.MANAGE_SYSTEM, Permissions.VIEW_SYSTEM_LOGS, Permissions.BACKUP_RESTORE,
                Permissions.MANAGE_SETTINGS,

                // Reports and Analytics
                Permissions.VIEW_REPORTS, Permissions.GENERATE_REPORTS, Permissions.VIEW_ANALYTICS,
                Permissions.EXPORT_DATA,

                // Staff Management
                Permissions.MANAGE_STAFF, Permissions.ASSIGN_ROLES, Permissions.VIEW_STAFF_PERFORMANCE,

                // Customer Service
                Permissions.HANDLE_CUSTOMER_QUERIES, Permissions.PROCESS_COMPLAINTS, Permissions.SEND_NOTIFICATIONS
        );
    }

    /**
     * Get role hierarchy level (higher number = more permissions)
     */
    public int getRoleLevel(ERole role) {
        switch (role) {
            case ADMIN: return 4;
            case MANAGER: return 3;
            case CASHIER: return 2;
            case USER: return 1;
            default: return 0;
        }
    }

    /**
     * Check if role1 has higher or equal privileges than role2
     */
    public boolean hasHigherOrEqualPrivileges(ERole role1, ERole role2) {
        return getRoleLevel(role1) >= getRoleLevel(role2);
    }

    /**
     * Get roles that can be assigned by the current role
     */
    public List<ERole> getAssignableRoles(ERole currentRole) {
        int currentLevel = getRoleLevel(currentRole);
        List<ERole> assignableRoles = new ArrayList<>();

        for (ERole role : ERole.values()) {
            if (getRoleLevel(role) < currentLevel) {
                assignableRoles.add(role);
            }
        }

        return assignableRoles;
    }

    /**
     * Get permission summary for a role
     */
    public Map<String, Object> getPermissionSummary(ERole role) {
        Map<String, Object> summary = new HashMap<>();
        List<String> permissions = getUserPermissions(role);

        summary.put("role", role.name());
        summary.put("totalPermissions", permissions.size());
        summary.put("permissions", permissions);
        summary.put("categorizedPermissions", getPermissionsByCategory(role));
        summary.put("roleLevel", getRoleLevel(role));
        summary.put("canAssignRoles", getAssignableRoles(role));

        return summary;
    }

    /**
     * Validate if a user can perform an action on a resource
     */
    public boolean canPerformAction(ERole userRole, String permission, ERole targetRole) {
        // Check if user has the required permission
        if (!hasPermission(userRole, permission)) {
            return false;
        }

        // For actions on other users, check role hierarchy
        if (targetRole != null) {
            return hasHigherOrEqualPrivileges(userRole, targetRole);
        }

        return true;
    }
}