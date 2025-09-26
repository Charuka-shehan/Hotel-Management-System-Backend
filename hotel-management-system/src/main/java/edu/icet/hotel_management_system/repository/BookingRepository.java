package edu.icet.hotel_management_system.repository;


import edu.icet.hotel_management_system.model.entity.Booking;
import edu.icet.hotel_management_system.model.entity.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);
    List<Booking> findByStatus(BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.checkInDate <= :date AND b.checkOutDate >= :date")
    List<Booking> findActiveBookingsOnDate(@Param("date") LocalDate date);

    @Query("SELECT b FROM Booking b WHERE b.checkInDate BETWEEN :startDate AND :endDate OR b.checkOutDate BETWEEN :startDate AND :endDate")
    List<Booking> findBookingsBetweenDates(@Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate);
}