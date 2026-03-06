package com.tripplannerai.dto.response.destination;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TotalDestinationResponse {
    private String code;
    private String message;
    private Map<String, List<DestinationTotalDto>> map;
    public static TotalDestinationResponse of(String code, String message, Map<String, List<DestinationTotalDto>> map) {
        return new TotalDestinationResponse(code, message, map);
    }
}
