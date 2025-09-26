package edu.icet.hotel_management_system.repository;

import edu.icet.hotel_management_system.model.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long>, JpaSpecificationExecutor<Room> {
    Optional<Room> findByRoomNumber(String roomNumber);
    List<Room> findByAvailableTrue();
    List<Room> findByTypeAndAvailableTrue(String type);
    List<Room> findByPriceBetweenAndAvailableTrue(BigDecimal minPrice, BigDecimal maxPrice);

    @Query("SELECT r FROM Room r WHERE r.id NOT IN " +
            "(SELECT b.room.id FROM Booking b WHERE " +
            "(b.checkInDate <= :checkOutDate AND b.checkOutDate >= :checkInDate) " +
            "AND b.status IN ('CONFIRMED', 'PENDING'))")
    List<Room> findAvailableRoomsForDates(@Param("checkInDate") LocalDate checkInDate,
                                          @Param("checkOutDate") LocalDate checkOutDate);
}