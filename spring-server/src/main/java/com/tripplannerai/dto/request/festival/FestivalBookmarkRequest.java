package com.tripplannerai.dto.request.festival;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class FestivalBookmarkRequest {
    private String contentId;
}
