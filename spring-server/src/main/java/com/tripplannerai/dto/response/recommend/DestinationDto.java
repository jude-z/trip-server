package com.tripplannerai.dto.response.recommend;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DestinationDto {
    private String destinationId;
    private String name;
    private String addr1;
    private String addr2;
    private Double latitude;
    private Double longitude;
    private String contentId;

}
