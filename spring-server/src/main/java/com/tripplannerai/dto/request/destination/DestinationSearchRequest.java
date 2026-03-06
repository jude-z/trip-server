package com.tripplannerai.dto.request.destination;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class DestinationSearchRequest {
    private List<String> contentIdList;

}
