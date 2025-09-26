package edu.icet.hotel_management_system.security;

public interface PermissionConstants {

    // User Management Permissions
    String VIEW_ALL_USERS = "VIEW_ALL_USERS";
    String CREATE_USER = "CREATE_USER";
    String UPDATE_USER = "UPDATE_USER";
    String DELETE_USER = "DELETE_USER";

    // Room Management Permissions
    String VIEW_ALL_ROOMS = "VIEW_ALL_ROOMS";
    String CREATE_ROOM = "CREATE_ROOM";
    String UPDATE_ROOM = "UPDATE_ROOM";
    String DELETE_ROOM = "DELETE_ROOM";

    // Booking Management Permissions
    String VIEW_ALL_BOOKINGS = "VIEW_ALL_BOOKINGS";
    String CREATE_BOOKING = "CREATE_BOOKING";
    String UPDATE_BOOKING = "UPDATE_BOOKING";
    String CONFIRM_BOOKING = "CONFIRM_BOOKING";

    // Payment Management Permissions
    String PROCESS_CASH_PAYMENTS = "PROCESS_CASH_PAYMENTS";
    String PROCESS_ONLINE_PAYMENTS = "PROCESS_ONLINE_PAYMENTS";
    String REFUND_PAYMENTS = "REFUND_PAYMENTS";
    String VIEW_PAYMENT_STATISTICS = "VIEW_PAYMENT_STATISTICS";

    // System Permissions
    String MANAGE_SYSTEM = "MANAGE_SYSTEM";
    String VIEW_REPORTS = "VIEW_REPORTS";
    String ASSIGN_ROLES = "ASSIGN_ROLES";
}