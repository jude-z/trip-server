package com.tripplannerai.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.tripplannerai.entity.destination.Destination;

public class DestinationFactory {
    public static Destination fromApiResponse(JsonNode destinationNode) {
        return Destination.builder()
                .addr1(destinationNode.path("addr1").asText())
                .addr2(destinationNode.path("addr2").asText())
                .contentId(destinationNode.path("contentid").asText())
                .tel(destinationNode.path("tel").asText().length()>100 ? "" : destinationNode.path("tel").asText() )
                .latitude(destinationNode.path("mapy").asText())
                .longitude(destinationNode.path("mapx").asText())
                .name(destinationNode.path("title").asText())
                .originImageUrl(destinationNode.path("firstimage").asText())
                .thumbnailImageUrl(destinationNode.path("firstimage2").asText())
                .build();

    }
}
