package edu.icet.hotel_management_system.controller;

import edu.icet.hotel_management_system.model.dto.RoomDto;
import edu.icet.hotel_management_system.service.RolePermissionService;
import edu.icet.hotel_management_system.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @Operation(summary = "Get all rooms")
    @PreAuthorize("@permissionEvaluator.hasPermission(T(edu.icet.hotel_management_system.service.RolePermissionService.Permissions).VIEW_ALL_ROOMS)")
    @GetMapping
    public ResponseEntity<List<RoomDto>> getAllRooms() {
        List<RoomDto> rooms = roomService.getAllRooms();
        return ResponseEntity.ok(rooms);
    }

    @Operation(summary = "Get available rooms")
    @PreAuthorize("@permissionEvaluator.hasPermission(T(edu.icet.hotel_management_system.service.RolePermissionService.Permissions).VIEW_ROOM_AVAILABILITY)")
    @GetMapping("/available")
    public ResponseEntity<List<RoomDto>> getAvailableRooms() {
        List<RoomDto> rooms = roomService.getAvailableRooms();
        return ResponseEntity.ok(rooms);
    }

    @Operation(summary = "Search rooms")
    @PreAuthorize("@permissionEvaluator.hasPermission(T(edu.icet.hotel_management_system.service.RolePermissionService.Permissions).SEARCH_ROOMS)")
    @GetMapping("/search")
    public ResponseEntity<List<RoomDto>> searchRooms(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {

        List<RoomDto> rooms = roomService.searchRooms(checkInDate, checkOutDate, type, minPrice, maxPrice);
        return ResponseEntity.ok(rooms);
    }

    @Operation(summary = "Create room (Admin only)")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("@permissionEvaluator.hasPermission(T(edu.icet.hotel_management_system.service.RolePermissionService.Permissions).CREATE_ROOM)")
    @PostMapping
    public ResponseEntity<RoomDto> createRoom(@Valid @RequestBody RoomDto roomDto) {
        RoomDto createdRoom = roomService.createRoom(roomDto);
        return new ResponseEntity<>(createdRoom, HttpStatus.CREATED);
    }

    @Operation(summary = "Update room (Admin only)")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("@permissionEvaluator.hasPermission(T(edu.icet.hotel_management_system.service.RolePermissionService.Permissions).UPDATE_ROOM)")
    @PutMapping("/{id}")
    public ResponseEntity<RoomDto> updateRoom(@PathVariable Long id, @Valid @RequestBody RoomDto roomDto) {
        RoomDto updatedRoom = roomService.updateRoom(id, roomDto);
        return ResponseEntity.ok(updatedRoom);
    }

    @Operation(summary = "Delete room (Admin only)")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("@permissionEvaluator.hasPermission(T(edu.icet.hotel_management_system.service.RolePermissionService.Permissions).DELETE_ROOM)")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.ok("Room deleted successfully");
    }

    @Operation(summary = "Upload room image (Admin only)")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("@permissionEvaluator.hasPermission(T(edu.icet.hotel_management_system.service.RolePermissionService.Permissions).UPLOAD_ROOM_IMAGE)")
    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RoomDto> uploadRoomImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        RoomDto updatedRoom = roomService.uploadRoomImage(id, file);
        return ResponseEntity.ok(updatedRoom);
    }

    @Operation(summary = "Delete room image (Admin only)")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("@permissionEvaluator.hasPermission(T(edu.icet.hotel_management_system.service.RolePermissionService.Permissions).DELETE_ROOM_IMAGE)")
    @DeleteMapping("/{roomId}/image/{imageId}")
    public ResponseEntity<RoomDto> deleteRoomImage(@PathVariable Long roomId, @PathVariable Long imageId) {
        RoomDto updatedRoom = roomService.deleteRoomImage(roomId, imageId);
        return ResponseEntity.ok(updatedRoom);
    }
}