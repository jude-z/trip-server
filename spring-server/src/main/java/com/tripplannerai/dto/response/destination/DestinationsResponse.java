package com.tripplannerai.dto.response.destination;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class DestinationsResponse {
    private List<DestinationResponse> content;
    private int totalCount;

    public static DestinationsResponse of(List<DestinationResponse> content, int totalCount) {
        return new DestinationsResponse(content, totalCount);
    }
}
