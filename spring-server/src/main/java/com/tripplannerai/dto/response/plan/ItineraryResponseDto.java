package com.tripplannerai.dto.response.plan;

import com.tripplannerai.dto.response.recommend.SpotDto;
import com.tripplannerai.entity.itinerary.Itinerary;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItineraryResponseDto {
    private Long id;
    private Integer day;

    public static ItineraryResponseDto fromEntity(Itinerary itinerary) {
       return ItineraryResponseDto.builder()
                .day(itinerary.getDay())
                .id(itinerary.getItineraryId())
                .build();
    }

    public static List<ItineraryResponseDto> fromEntities(List<Itinerary> itineraries) {
        return itineraries.stream()
                .map(ItineraryResponseDto::fromEntity)
                .toList();
    }
}
