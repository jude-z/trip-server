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
public class DestinationsCategoryResponse {
    private String code;
    private String message;
    private List<DestinationCategoryDto> content;
    private int totalCount;

    public static DestinationsCategoryResponse of(String code, String message, List<DestinationCategoryDto> content, int totalCount) {
        return new DestinationsCategoryResponse(code,message,content, totalCount);
    }
}
