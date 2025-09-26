package edu.icet.hotel_management_system.service.impl;

import edu.icet.hotel_management_system.exception.ResourceNotFoundException;
import edu.icet.hotel_management_system.model.dto.RoomDto;
import edu.icet.hotel_management_system.model.entity.Room;
import edu.icet.hotel_management_system.model.entity.RoomImage;
import edu.icet.hotel_management_system.repository.RoomRepository;
import edu.icet.hotel_management_system.service.RoomService;
import edu.icet.hotel_management_system.util.FileUploadUtil;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RoomServiceImpl implements RoomService {

    private static final Logger logger = LoggerFactory.getLogger(RoomServiceImpl.class);

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public RoomDto createRoom(RoomDto roomDto) {
        logger.info("Creating room with number: {}", roomDto.getRoomNumber());
        Room room = modelMapper.map(roomDto, Room.class);
        Room savedRoom = roomRepository.save(room);
        return modelMapper.map(savedRoom, RoomDto.class);
    }

    @Override
    public RoomDto getRoomById(Long id) {
        logger.info("Fetching room with id: {}", id);
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "id", id));
        return modelMapper.map(room, RoomDto.class);
    }

    @Override
    @Cacheable(value = "availableRooms")
    public List<RoomDto> getAllRooms() {
        logger.info("Fetching all rooms");
        List<Room> rooms = roomRepository.findAll();
        return rooms.stream()
                .map(room -> modelMapper.map(room, RoomDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "availableRooms")
    public List<RoomDto> getAvailableRooms() {
        logger.info("Fetching available rooms");
        List<Room> rooms = roomRepository.findByAvailableTrue();
        return rooms.stream()
                .map(room -> modelMapper.map(room, RoomDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @CacheEvict(value = "availableRooms", allEntries = true)
    public RoomDto updateRoom(Long id, RoomDto roomDto) {
        logger.info("Updating room with id: {}", id);
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "id", id));

        room.setRoomNumber(roomDto.getRoomNumber());
        room.setType(roomDto.getType());
        room.setPrice(roomDto.getPrice());
        room.setDescription(roomDto.getDescription());
        room.setMaxOccupancy(roomDto.getMaxOccupancy());
        room.setAvailable(roomDto.isAvailable());
        room.setAmenities(roomDto.getAmenities());

        Room updatedRoom = roomRepository.save(room);
        return modelMapper.map(updatedRoom, RoomDto.class);
    }

    @Override
    @CacheEvict(value = "availableRooms", allEntries = true)
    public void deleteRoom(Long id) {
        logger.info("Deleting room with id: {}", id);
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "id", id));
        roomRepository.delete(room);
    }

    @Override
    @CacheEvict(value = "availableRooms", allEntries = true)
    public RoomDto uploadRoomImage(Long roomId, MultipartFile file) {
        logger.info("Uploading image for room id: {}", roomId);
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "id", roomId));

        try {
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            String filePath = uploadDir + "/rooms/" + fileName;

            FileUploadUtil.saveFile(uploadDir + "/rooms", fileName, file);

            RoomImage roomImage = new RoomImage();
            roomImage.setImageUrl("/uploads/rooms/" + fileName);
            roomImage.setRoom(room);

            room.getImages().add(roomImage);
            Room updatedRoom = roomRepository.save(room);

            return modelMapper.map(updatedRoom, RoomDto.class);
        } catch (IOException e) {
            logger.error("Failed to upload image for room id: {}", roomId, e);
            throw new RuntimeException("Failed to upload image: " + e.getMessage());
        }
    }

    @Override
    @CacheEvict(value = "availableRooms", allEntries = true)
    public RoomDto deleteRoomImage(Long roomId, Long imageId) {
        logger.info("Deleting image id: {} for room id: {}", imageId, roomId);
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "id", roomId));

        RoomImage imageToRemove = room.getImages().stream()
                .filter(image -> image.getId().equals(imageId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("RoomImage", "id", imageId));

        try {
            String imagePath = uploadDir + imageToRemove.getImageUrl().replace("/uploads", "");
            FileUploadUtil.deleteFile(imagePath);
        } catch (IOException e) {
            logger.error("Failed to delete image file for image id: {}", imageId, e);
            throw new RuntimeException("Failed to delete image file: " + e.getMessage());
        }

        room.getImages().remove(imageToRemove);
        Room updatedRoom = roomRepository.save(room);

        return modelMapper.map(updatedRoom, RoomDto.class);
    }

    @Override
    public List<RoomDto> searchRooms(LocalDate checkInDate, LocalDate checkOutDate,
                                     String type, BigDecimal minPrice, BigDecimal maxPrice) {
        logger.info("Searching rooms from {} to {}, type: {}, minPrice: {}, maxPrice: {}",
                checkInDate, checkOutDate, type, minPrice, maxPrice);
        List<Room> rooms;

        if (checkInDate != null && checkOutDate != null) {
            rooms = roomRepository.findAvailableRoomsForDates(checkInDate, checkOutDate);
        } else {
            rooms = roomRepository.findByAvailableTrue();
        }

        if (type != null && !type.isEmpty()) {
            rooms = rooms.stream()
                    .filter(room -> room.getType().equalsIgnoreCase(type))
                    .collect(Collectors.toList());
        }

        if (minPrice != null) {
            rooms = rooms.stream()
                    .filter(room -> room.getPrice().compareTo(minPrice) >= 0)
                    .collect(Collectors.toList());
        }

        if (maxPrice != null) {
            rooms = rooms.stream()
                    .filter(room -> room.getPrice().compareTo(maxPrice) <= 0)
                    .collect(Collectors.toList());
        }

        return rooms.stream()
                .map(room -> modelMapper.map(room, RoomDto.class))
                .collect(Collectors.toList());
    }
}