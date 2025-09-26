package edu.icet.hotel_management_system.service.impl;

import edu.icet.hotel_management_system.exception.ResourceNotFoundException;
import edu.icet.hotel_management_system.model.dto.BookingDto;
import edu.icet.hotel_management_system.model.dto.RoomDto;
import edu.icet.hotel_management_system.model.entity.Booking;
import edu.icet.hotel_management_system.model.entity.Room;
import edu.icet.hotel_management_system.model.entity.User;
import edu.icet.hotel_management_system.model.entity.enums.BookingStatus;
import edu.icet.hotel_management_system.repository.BookingRepository;
import edu.icet.hotel_management_system.repository.RoomRepository;
import edu.icet.hotel_management_system.repository.UserRepository;
import edu.icet.hotel_management_system.service.BookingService;
import edu.icet.hotel_management_system.service.EmailService;
import edu.icet.hotel_management_system.util.BookingUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    private static final Logger logger = LoggerFactory.getLogger(BookingServiceImpl.class);

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<BookingDto> getAllBookings(int page, int size) {
        logger.info("Fetching all bookings, page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Booking> bookingPage = bookingRepository.findAll(pageable);
        return bookingPage.stream()
                .map(booking -> {
                    BookingDto dto = modelMapper.map(booking, BookingDto.class);
                    dto.setStatus(booking.getStatus().name()); // Convert enum to string
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getUserBookings(Long userId) {
        logger.info("Fetching bookings for userId: {}", userId);

        // Verify user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        List<Booking> bookings = bookingRepository.findByUserId(userId);
        return bookings.stream()
                .map(booking -> {
                    BookingDto dto = modelMapper.map(booking, BookingDto.class);
                    dto.setStatus(booking.getStatus().name());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> searchBookings(LocalDate startDate, LocalDate endDate, String status) {
        logger.info("Searching bookings from {} to {}, status: {}", startDate, endDate, status);
        List<Booking> bookings;

        if (startDate != null && endDate != null) {
            bookings = bookingRepository.findBookingsBetweenDates(startDate, endDate);
        } else {
            bookings = bookingRepository.findAll(Sort.by("id").descending());
        }

        if (status != null && !status.isEmpty()) {
            try {
                BookingStatus bookingStatus = BookingStatus.valueOf(status.toUpperCase());
                bookings = bookings.stream()
                        .filter(booking -> booking.getStatus() == bookingStatus)
                        .collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid booking status: {}", status);
            }
        }

        return bookings.stream()
                .map(booking -> {
                    BookingDto dto = modelMapper.map(booking, BookingDto.class);
                    dto.setStatus(booking.getStatus().name());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public BookingDto createBooking(BookingDto bookingDto) {
        logger.info("Creating booking for userId: {}, roomId: {}", bookingDto.getUserId(), bookingDto.getRoomId());

        // Validate input
        if (bookingDto.getCheckInDate().isAfter(bookingDto.getCheckOutDate())) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }

        if (bookingDto.getCheckInDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Check-in date cannot be in the past");
        }

        User user = userRepository.findById(bookingDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", bookingDto.getUserId()));

        Room room = roomRepository.findById(bookingDto.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room", "id", bookingDto.getRoomId()));

        // Check if room is available
        if (!room.isAvailable()) {
            throw new IllegalStateException("Room is not available for booking");
        }

        // Check room availability for the requested dates
        List<Room> availableRooms = roomRepository.findAvailableRoomsForDates(
                bookingDto.getCheckInDate(), bookingDto.getCheckOutDate());

        if (!availableRooms.contains(room)) {
            throw new IllegalStateException("Room is not available for the selected dates");
        }

        // Validate number of guests
        if (bookingDto.getNumberOfGuests() > room.getMaxOccupancy()) {
            throw new IllegalArgumentException(
                    String.format("Number of guests (%d) exceeds room capacity (%d)",
                            bookingDto.getNumberOfGuests(), room.getMaxOccupancy()));
        }

        Booking booking = modelMapper.map(bookingDto, Booking.class);
        booking.setUser(user);
        booking.setRoom(room);

        // Calculate total price
        BigDecimal totalPrice = BookingUtils.calculateTotalPrice(room,
                bookingDto.getCheckInDate(), bookingDto.getCheckOutDate());
        booking.setTotalPrice(totalPrice);
        booking.setStatus(BookingStatus.PENDING);

        Booking savedBooking = bookingRepository.save(booking);

        // Send confirmation email asynchronously
        try {
            emailService.sendBookingConfirmationEmail(user, savedBooking);
        } catch (Exception e) {
            logger.error("Failed to send booking confirmation email for booking {}", savedBooking.getId(), e);
            // Don't fail the booking creation if email fails
        }

        BookingDto resultDto = modelMapper.map(savedBooking, BookingDto.class);
        resultDto.setStatus(savedBooking.getStatus().name());
        return resultDto;
    }

    @Override
    public BookingDto getBookingById(Long id) {
        logger.info("Fetching booking by id: {}", id);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", id));

        BookingDto dto = modelMapper.map(booking, BookingDto.class);
        dto.setStatus(booking.getStatus().name());
        return dto;
    }

    @Override
    public BookingDto updateBooking(Long id, BookingDto bookingDto) {
        logger.info("Updating booking id: {}", id);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", id));

        // Only allow updates for pending bookings
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException("Only pending bookings can be updated");
        }

        // Validate new dates if provided
        if (bookingDto.getCheckInDate() != null && bookingDto.getCheckOutDate() != null) {
            if (bookingDto.getCheckInDate().isAfter(bookingDto.getCheckOutDate())) {
                throw new IllegalArgumentException("Check-out date must be after check-in date");
            }

            if (bookingDto.getCheckInDate().isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("Check-in date cannot be in the past");
            }
        }

        // Update fields
        if (bookingDto.getCheckInDate() != null) {
            booking.setCheckInDate(bookingDto.getCheckInDate());
        }
        if (bookingDto.getCheckOutDate() != null) {
            booking.setCheckOutDate(bookingDto.getCheckOutDate());
        }
        if (bookingDto.getNumberOfGuests() > 0) {
            // Validate against room capacity
            if (bookingDto.getNumberOfGuests() > booking.getRoom().getMaxOccupancy()) {
                throw new IllegalArgumentException("Number of guests exceeds room capacity");
            }
            booking.setNumberOfGuests(bookingDto.getNumberOfGuests());
        }
        if (bookingDto.getSpecialRequests() != null) {
            booking.setSpecialRequests(bookingDto.getSpecialRequests());
        }

        // Recalculate price if dates changed
        BigDecimal newTotalPrice = BookingUtils.calculateTotalPrice(booking.getRoom(),
                booking.getCheckInDate(), booking.getCheckOutDate());
        booking.setTotalPrice(newTotalPrice);

        Booking updatedBooking = bookingRepository.save(booking);

        BookingDto resultDto = modelMapper.map(updatedBooking, BookingDto.class);
        resultDto.setStatus(updatedBooking.getStatus().name());
        return resultDto;
    }

    @Override
    public BookingDto cancelBooking(Long id) {
        logger.info("Cancelling booking id: {}", id);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", id));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Booking is already cancelled");
        }

        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel a completed booking");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        Booking cancelledBooking = bookingRepository.save(booking);

        // Send cancellation email asynchronously
        try {
            User user = userRepository.findById(cancelledBooking.getUser().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", cancelledBooking.getUser().getId()));
            emailService.sendBookingCancellationEmail(user, cancelledBooking);
        } catch (Exception e) {
            logger.error("Failed to send booking cancellation email for booking {}", cancelledBooking.getId(), e);
        }

        BookingDto resultDto = modelMapper.map(cancelledBooking, BookingDto.class);
        resultDto.setStatus(cancelledBooking.getStatus().name());
        return resultDto;
    }

    @Override
    public BookingDto confirmBooking(Long id) {
        logger.info("Confirming booking id: {}", id);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", id));

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException("Only pending bookings can be confirmed");
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        Booking confirmedBooking = bookingRepository.save(booking);

        BookingDto resultDto = modelMapper.map(confirmedBooking, BookingDto.class);
        resultDto.setStatus(confirmedBooking.getStatus().name());
        return resultDto;
    }

    @Override
    public BookingDto completeBooking(Long id) {
        logger.info("Completing booking id: {}", id);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", id));

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Only confirmed bookings can be completed");
        }

        booking.setStatus(BookingStatus.COMPLETED);
        Booking completedBooking = bookingRepository.save(booking);

        BookingDto resultDto = modelMapper.map(completedBooking, BookingDto.class);
        resultDto.setStatus(completedBooking.getStatus().name());
        return resultDto;
    }

    @Override
    public void deleteBooking(Long id) {
        logger.info("Deleting booking id: {}", id);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", id));

        // Only allow deletion of cancelled bookings
        if (booking.getStatus() != BookingStatus.CANCELLED && booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException("Only cancelled or pending bookings can be deleted");
        }

        bookingRepository.delete(booking);
    }

    @Override
    public List<RoomDto> getAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, Integer guests) {
        logger.info("Finding available rooms from {} to {}, guests: {}", checkInDate, checkOutDate, guests);

        if (checkInDate == null || checkOutDate == null) {
            throw new IllegalArgumentException("Check-in and check-out dates are required");
        }

        if (checkInDate.isAfter(checkOutDate)) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }

        List<Room> availableRooms = roomRepository.findAvailableRoomsForDates(checkInDate, checkOutDate);

        if (guests != null && guests > 0) {
            availableRooms = availableRooms.stream()
                    .filter(room -> room.getMaxOccupancy() >= guests)
                    .collect(Collectors.toList());
        }

        return availableRooms.stream()
                .map(room -> modelMapper.map(room, RoomDto.class))
                .collect(Collectors.toList());
    }
}