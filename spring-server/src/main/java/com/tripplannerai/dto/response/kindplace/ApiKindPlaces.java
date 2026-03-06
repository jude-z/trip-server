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
public class ApiKindPlaces {
    private Integer page;
    private Integer perPage;
    private Integer totalCount;
    private Integer currentCount;
    private Integer matchCount;
    private List<ApiKindPlace> data;


}
