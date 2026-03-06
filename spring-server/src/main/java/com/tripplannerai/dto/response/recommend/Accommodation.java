package com.tripplannerai.dto.response.recommend;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tripplannerai.entity.destination.Destination;
import com.tripplannerai.entity.tourspot.TourSpot;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Accommodation {
    @JsonProperty("destination_id")
    private String destinationId;
    private String name;
    private String addr1;
    private String addr2;
    private Double latitude;
    private Double longitude;
    @JsonProperty("content_id")
    private String contentId;

    public static Accommodation fromEntity(TourSpot tourSpot) {
        Destination destination = tourSpot.getDestination();
        return Accommodation.builder()
                .destinationId(destination.getDestinationId().toString())
                .name(destination.getName())
                .addr1(destination.getAddr1())
                .addr2(destination.getAddr2())
                .latitude(Double.parseDouble(destination.getLatitude()))
                .longitude(Double.parseDouble(destination.getLongitude()))
                .contentId(destination.getContentId())
                .build();
    }
}
