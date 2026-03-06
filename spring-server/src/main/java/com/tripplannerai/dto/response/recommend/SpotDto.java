package com.tripplannerai.dto.response.recommend;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tripplannerai.entity.destination.Destination;
import com.tripplannerai.entity.tourspot.TourSpot;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SpotDto {
    @JsonProperty("destination_id")
    private String destinationId;
    private String name;
    private String addr1;
    private String addr2;
    private Double latitude;
    private Double longitude;
    @JsonProperty("content_id")
    private String contentId;
    @JsonProperty("category_code")
    private String categoryCode;
    @JsonProperty("category_name")
    private String categoryName;
    private String type;

    public static SpotDto fromEntity(TourSpot tourSpot) {
        Destination destination = tourSpot.getDestination();
        String categoryCode = destination.getCategory().getCategoryCode();
        return SpotDto.builder()
                .destinationId(destination.getDestinationId().toString())
                .name(destination.getName())
                .addr1(destination.getAddr1())
                .addr2(destination.getAddr2())
                .latitude(Double.parseDouble(destination.getLatitude()))
                .longitude(Double.parseDouble(destination.getLongitude()))
                .contentId(destination.getContentId())
                .categoryCode(destination.getCategory().getCategoryCode())
                .categoryName(destination.getCategory().getName())
                .build();
    }
}
