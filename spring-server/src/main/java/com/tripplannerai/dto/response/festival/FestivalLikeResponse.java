package com.tripplannerai.dto.response.festival;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FestivalLikeResponse {
    private String code;
    private String message;

    public static FestivalLikeResponse of (String code, String message) {
        return new FestivalLikeResponse(code, message);}
}
