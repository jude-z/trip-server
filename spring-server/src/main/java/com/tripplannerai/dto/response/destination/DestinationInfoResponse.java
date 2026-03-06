package com.tripplannerai.dto.response.destination;

import com.tripplannerai.entity.destination.Destination;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DestinationInfoResponse {
    private Long destinationId;
    private String destinationName;
    private String destinationAddr1;
    private String destinationAddr2;
    private String destinationContentId;
    private String destinationLatitude;
    private String destinationLongitude;
    private String destinationRating;
    private String destinationTel;
    private String destinationOriginImageUrl;
    private String destinationThumbnailImageUrl;

    public static DestinationInfoResponse fromEntity(Destination destination){
        return DestinationInfoResponse.builder()
                .destinationId(destination.getDestinationId())
                .destinationName(destination.getName())
                .destinationAddr1(destination.getAddr1())
                .destinationAddr2(destination.getAddr2())
                .destinationContentId(destination.getContentId())
                .destinationLatitude(destination.getLatitude())
                .destinationLongitude(destination.getLongitude())
                .destinationRating(destination.getRating())
                .destinationTel(destination.getTel())
                .destinationOriginImageUrl(destination.getOriginImageUrl())
                .destinationThumbnailImageUrl(destination.getThumbnailImageUrl())
                .build();
    }

}
