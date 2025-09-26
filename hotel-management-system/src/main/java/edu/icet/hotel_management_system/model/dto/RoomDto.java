package edu.icet.hotel_management_system.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Set;
import java.util.HashSet;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Room data transfer object")
public class RoomDto {

    @Schema(description = "Room ID", example = "1")
    private Long id;

    @NotBlank(message = "Room number is required")
    @Size(max = 10, message = "Room number must not exceed 10 characters")
    @Schema(description = "Room number", example = "101", required = true)
    private String roomNumber;

    @NotBlank(message = "Room type is required")
    @Pattern(regexp = "^(Single|Double|Twin|Suite|Deluxe|Family|Executive|Presidential)$",
            message = "Invalid room type")
    @Schema(description = "Room type", example = "Deluxe", required = true,
            allowableValues = {"Single", "Double", "Twin", "Suite", "Deluxe", "Family", "Executive", "Presidential"})
    private String type;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Price must be a valid monetary amount")
    @Schema(description = "Price per night in USD", example = "199.99", required = true)
    private BigDecimal price;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Schema(description = "Room description", example = "Luxurious deluxe room with ocean view")
    private String description;

    @Min(value = 1, message = "Max occupancy must be at least 1")
    @Max(value = 10, message = "Max occupancy must not exceed 10")
    @Schema(description = "Maximum number of guests", example = "2", required = true)
    private int maxOccupancy;

    @Schema(description = "Room availability status", example = "true")
    private boolean available = true;

    @Schema(description = "Room amenities",
            example = "[\"WiFi\", \"Air Conditioning\", \"Mini Bar\", \"Ocean View\"]")
    private Set<String> amenities = new HashSet<>();

    @Schema(description = "Room image URLs",
            example = "[\"/uploads/rooms/room_1_image1.jpg\", \"/uploads/rooms/room_1_image2.jpg\"]")
    private Set<String> imageUrls = new HashSet<>();

    // Additional computed fields
    @Schema(description = "Number of available amenities", accessMode = Schema.AccessMode.READ_ONLY)
    public int getAmenityCount() {
        return amenities != null ? amenities.size() : 0;
    }

    @Schema(description = "Number of images", accessMode = Schema.AccessMode.READ_ONLY)
    public int getImageCount() {
        return imageUrls != null ? imageUrls.size() : 0;
    }

    @Schema(description = "Has images flag", accessMode = Schema.AccessMode.READ_ONLY)
    public boolean hasImages() {
        return imageUrls != null && !imageUrls.isEmpty();
    }

    @Schema(description = "Primary image URL (first image)", accessMode = Schema.AccessMode.READ_ONLY)
    public String getPrimaryImageUrl() {
        if (imageUrls != null && !imageUrls.isEmpty()) {
            return imageUrls.iterator().next();
        }
        return null;
    }

    // Helper methods for amenities
    public void addAmenity(String amenity) {
        if (amenities == null) {
            amenities = new HashSet<>();
        }
        if (amenity != null && !amenity.trim().isEmpty()) {
            amenities.add(amenity.trim());
        }
    }

    public void removeAmenity(String amenity) {
        if (amenities != null) {
            amenities.remove(amenity);
        }
    }

    public boolean hasAmenity(String amenity) {
        return amenities != null && amenities.contains(amenity);
    }

    // Helper methods for images
    public void addImageUrl(String imageUrl) {
        if (imageUrls == null) {
            imageUrls = new HashSet<>();
        }
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            imageUrls.add(imageUrl.trim());
        }
    }

    public void removeImageUrl(String imageUrl) {
        if (imageUrls != null) {
            imageUrls.remove(imageUrl);
        }
    }

    public void clearImages() {
        if (imageUrls != null) {
            imageUrls.clear();
        }
    }

    // Validation methods
    @Schema(description = "Price range category", accessMode = Schema.AccessMode.READ_ONLY)
    public String getPriceCategory() {
        if (price == null) return "UNKNOWN";

        BigDecimal priceValue = price;
        if (priceValue.compareTo(BigDecimal.valueOf(100)) < 0) {
            return "BUDGET";
        } else if (priceValue.compareTo(BigDecimal.valueOf(300)) < 0) {
            return "STANDARD";
        } else if (priceValue.compareTo(BigDecimal.valueOf(500)) < 0) {
            return "PREMIUM";
        } else {
            return "LUXURY";
        }
    }

    @Schema(description = "Room capacity category", accessMode = Schema.AccessMode.READ_ONLY)
    public String getCapacityCategory() {
        if (maxOccupancy <= 1) return "SINGLE";
        if (maxOccupancy <= 2) return "COUPLE";
        if (maxOccupancy <= 4) return "FAMILY";
        return "GROUP";
    }

    // Utility method to get full image URLs
    public Set<String> getFullImageUrls(String baseUrl) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return new HashSet<>();
        }

        return imageUrls.stream()
                .map(url -> url.startsWith("http") ? url : baseUrl + url)
                .collect(java.util.stream.Collectors.toSet());
    }
}