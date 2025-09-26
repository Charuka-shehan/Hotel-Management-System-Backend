package edu.icet.hotel_management_system.service;

import edu.icet.hotel_management_system.model.dto.RoomDto;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface RoomService {
    RoomDto createRoom(RoomDto roomDto);
    RoomDto getRoomById(Long id);
    List<RoomDto> getAllRooms();
    List<RoomDto> getAvailableRooms();
    RoomDto updateRoom(Long id, RoomDto roomDto);
    void deleteRoom(Long id);
    RoomDto uploadRoomImage(Long roomId, MultipartFile file);
    RoomDto deleteRoomImage(Long roomId, Long imageId);
    List<RoomDto> searchRooms(LocalDate checkInDate, LocalDate checkOutDate,
                              String type, BigDecimal minPrice, BigDecimal maxPrice);
}