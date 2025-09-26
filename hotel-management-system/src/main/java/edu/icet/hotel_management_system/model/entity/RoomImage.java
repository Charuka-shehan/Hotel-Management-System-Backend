
package edu.icet.hotel_management_system.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Objects;

@Entity
@Table(name = "room_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "room") // Exclude room from toString to avoid circular reference
public class RoomImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    // Custom equals and hashCode methods excluding the room field
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoomImage roomImage = (RoomImage) o;
        return Objects.equals(id, roomImage.id) &&
                Objects.equals(imageUrl, roomImage.imageUrl);
        // Note: room field is excluded to avoid circular references
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, imageUrl);
        // Note: room field is excluded to avoid circular references
    }
}
