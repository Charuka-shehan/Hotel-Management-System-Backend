package edu.icet.hotel_management_system.util;

import edu.icet.hotel_management_system.model.entity.Room;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class BookingUtils {
    public static BigDecimal calculateTotalPrice(Room room, LocalDate checkInDate, LocalDate checkOutDate) {
        long numberOfNights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        return room.getPrice().multiply(BigDecimal.valueOf(numberOfNights));
    }
}