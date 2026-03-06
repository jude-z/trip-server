package com.tripplannerai.dto.response.kindplace;

import com.tripplannerai.entity.kindplace.KindPlace;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FetchKindPlaces {
    private String code;
    private String message;
    private List<KindPlace> list;

    public static FetchKindPlaces of(String code, String message, List<KindPlace> list) {
        return new FetchKindPlaces(code,message,list);
    }
}
