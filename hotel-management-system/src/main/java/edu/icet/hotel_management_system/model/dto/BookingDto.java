package edu.icet.hotel_management_system.model.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BookingDto {
    private Long id;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private int numberOfGuests;
    private String specialRequests;
    private BigDecimal totalPrice;
    private String status;
    private Long userId;
    private Long roomId;
    private RoomDto room;
    private UserDto user;
}