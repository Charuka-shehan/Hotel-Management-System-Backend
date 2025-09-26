package edu.icet.hotel_management_system.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"bookings", "images"}) // Exclude collections from toString to avoid lazy loading issues
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String roomNumber;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private int maxOccupancy;

    @Column(nullable = false)
    private boolean available = true;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "room_amenities", joinColumns = @JoinColumn(name = "room_id"))
    @Column(name = "amenity")
    private Set<String> amenities = new HashSet<>();

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Booking> bookings = new HashSet<>();

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<RoomImage> images = new HashSet<>();

    // Custom equals and hashCode methods excluding relationship fields
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return maxOccupancy == room.maxOccupancy &&
                available == room.available &&
                Objects.equals(id, room.id) &&
                Objects.equals(roomNumber, room.roomNumber) &&
                Objects.equals(type, room.type) &&
                Objects.equals(price, room.price) &&
                Objects.equals(description, room.description);
        // Note: amenities, bookings, and images are excluded to avoid circular references
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, roomNumber, type, price, description, maxOccupancy, available);
        // Note: amenities, bookings, and images are excluded to avoid circular references
    }
}