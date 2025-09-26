package edu.icet.hotel_management_system.controller;

import edu.icet.hotel_management_system.model.dto.BookingDto;
import edu.icet.hotel_management_system.model.dto.RoomDto;
import edu.icet.hotel_management_system.service.BookingService;
import edu.icet.hotel_management_system.service.RolePermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@SecurityRequirement(name = "bearerAuth")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Operation(summary = "Get all bookings (Admin/Manager only)")
    @PreAuthorize("@permissionEvaluator.hasPermission(T(edu.icet.hotel_management_system.service.RolePermissionService.Permissions).VIEW_ALL_BOOKINGS)")
    @GetMapping
    public ResponseEntity<List<BookingDto>> getAllBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<BookingDto> bookings = bookingService.getAllBookings(page, size);
        return ResponseEntity.ok(bookings);
    }

    @Operation(summary = "Get user bookings")
    @PreAuthorize("@permissionEvaluator.hasPermission(T(edu.icet.hotel_management_system.service.RolePermissionService.Permissions).VIEW_ALL_BOOKINGS) or " +
            "@permissionEvaluator.canAccessUserResource(#userId)")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingDto>> getUserBookings(@PathVariable Long userId) {
        List<BookingDto> bookings = bookingService.getUserBookings(userId);
        return ResponseEntity.ok(bookings);
    }

    @Operation(summary = "Get booking by ID")
    @PreAuthorize("@permissionEvaluator.hasPermission(T(edu.icet.hotel_management_system.service.RolePermissionService.Permissions).VIEW_ALL_BOOKINGS) or " +
            "@permissionEvaluator.canAccessBooking(#id)")
    @GetMapping("/{id}")
    public ResponseEntity<BookingDto> getBookingById(@PathVariable Long id) {
        BookingDto booking = bookingService.getBookingById(id);
        return ResponseEntity.ok(booking);
    }


    @Operation(summary = "Create booking")
    @PreAuthorize("@permissionEvaluator.hasPermission(T(edu.icet.hotel_management_system.service.RolePermissionService.Permissions).CREATE_BOOKING)")
    @PostMapping
    public ResponseEntity<BookingDto> createBooking(@Valid @RequestBody BookingDto bookingDto) {
        BookingDto createdBooking = bookingService.createBooking(bookingDto);
        return new ResponseEntity<>(createdBooking, HttpStatus.CREATED);
    }

    @Operation(summary = "Update booking")
    @PreAuthorize("@permissionEvaluator.hasPermission(T(edu.icet.hotel_management_system.service.RolePermissionService.Permissions).UPDATE_BOOKING)")
    @PutMapping("/{id}")
    public ResponseEntity<BookingDto> updateBooking(@PathVariable Long id, @Valid @RequestBody BookingDto bookingDto) {
        BookingDto updatedBooking = bookingService.updateBooking(id, bookingDto);
        return ResponseEntity.ok(updatedBooking);
    }

    @Operation(summary = "Cancel booking")
    @PreAuthorize("@permissionEvaluator.hasPermission(T(edu.icet.hotel_management_system.service.RolePermissionService.Permissions).CANCEL_BOOKING)")
    @PutMapping("/{id}/cancel")
    public ResponseEntity<BookingDto> cancelBooking(@PathVariable Long id) {
        BookingDto cancelledBooking = bookingService.cancelBooking(id);
        return ResponseEntity.ok(cancelledBooking);
    }

    @Operation(summary = "Confirm booking (Admin/Manager only)")
    @PreAuthorize("@permissionEvaluator.hasPermission(T(edu.icet.hotel_management_system.service.RolePermissionService.Permissions).CONFIRM_BOOKING)")
    @PutMapping("/{id}/confirm")
    public ResponseEntity<BookingDto> confirmBooking(@PathVariable Long id) {
        BookingDto confirmedBooking = bookingService.confirmBooking(id);
        return ResponseEntity.ok(confirmedBooking);
    }

    @Operation(summary = "Complete booking (Admin/Manager only)")
    @PreAuthorize("@permissionEvaluator.hasPermission(T(edu.icet.hotel_management_system.service.RolePermissionService.Permissions).COMPLETE_BOOKING)")
    @PutMapping("/{id}/complete")
    public ResponseEntity<BookingDto> completeBooking(@PathVariable Long id) {
        BookingDto completedBooking = bookingService.completeBooking(id);
        return ResponseEntity.ok(completedBooking);
    }

    @Operation(summary = "Delete booking (Admin only)")
    @PreAuthorize("@permissionEvaluator.hasPermission(T(edu.icet.hotel_management_system.service.RolePermissionService.Permissions).DELETE_BOOKING)")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.ok("Booking deleted successfully");
    }

    @Operation(summary = "Check room availability")
    @PreAuthorize("@permissionEvaluator.hasPermission(T(edu.icet.hotel_management_system.service.RolePermissionService.Permissions).VIEW_ROOM_AVAILABILITY)")
    @GetMapping("/availability")
    public ResponseEntity<List<RoomDto>> checkAvailability(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
            @RequestParam(required = false) Integer guests) {
        List<RoomDto> availableRooms = bookingService.getAvailableRooms(checkInDate, checkOutDate, guests);
        return ResponseEntity.ok(availableRooms);
    }

    @Operation(summary = "Search bookings (Admin/Manager only)")
    @PreAuthorize("@permissionEvaluator.hasPermission(T(edu.icet.hotel_management_system.service.RolePermissionService.Permissions).SEARCH_BOOKINGS)")
    @GetMapping("/search")
    public ResponseEntity<List<BookingDto>> searchBookings(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String status) {
        List<BookingDto> bookings = bookingService.searchBookings(startDate, endDate, status);
        return ResponseEntity.ok(bookings);
    }
}