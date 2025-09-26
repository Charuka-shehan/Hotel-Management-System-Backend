package edu.icet.hotel_management_system.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import edu.icet.hotel_management_system.model.entity.Room;
import edu.icet.hotel_management_system.model.entity.RoomImage;
import edu.icet.hotel_management_system.model.dto.RoomDto;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setSkipNullEnabled(true);

        // Custom mapping for Room to RoomDto with null safety
        modelMapper.createTypeMap(Room.class, RoomDto.class)
                .addMapping(src -> {
                    Set<RoomImage> images = src.getImages();
                    if (images == null || images.isEmpty()) {
                        return Collections.emptySet();
                    }
                    return images.stream()
                            .map(RoomImage::getImageUrl)
                            .collect(Collectors.toSet());
                }, RoomDto::setImageUrls);

        return modelMapper;
    }
}