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
public class DestinationResponse {
    private String code;
    private String message;
    private String contentId;
    private String thumbnailImageUrl;
    private Long destinationId;

    public static DestinationResponse of(DestinationQuery destinationQuery) {
        return DestinationResponse.builder()
                .contentId(destinationQuery.getContentId())
                .destinationId(destinationQuery.getDestinationId())
                .build();
    }

    public static DestinationResponse of(String code, String message,Destination destination) {
        return new DestinationResponse(code, message,destination.getContentId(),destination.getThumbnailImageUrl(),destination.getDestinationId());
    }
}
