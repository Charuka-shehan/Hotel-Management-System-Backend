package edu.icet.hotel_management_system.service;

import edu.icet.hotel_management_system.model.dto.BookingDto;
import edu.icet.hotel_management_system.model.dto.RoomDto;

import java.time.LocalDate;
import java.util.List;

public interface BookingService {

    // Core CRUD operations
    BookingDto createBooking(BookingDto bookingDto);
    BookingDto getBookingById(Long id);
    BookingDto updateBooking(Long id, BookingDto bookingDto);
    void deleteBooking(Long id);

    // Status management
    BookingDto confirmBooking(Long id);
    BookingDto cancelBooking(Long id);
    BookingDto completeBooking(Long id);

    // Listing and filtering
    List<BookingDto> getAllBookings(int page, int size);
    List<BookingDto> getUserBookings(Long userId);
    List<BookingDto> searchBookings(LocalDate startDate, LocalDate endDate, String status);

    // Availability checking
    List<RoomDto> getAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, Integer guests);
}
