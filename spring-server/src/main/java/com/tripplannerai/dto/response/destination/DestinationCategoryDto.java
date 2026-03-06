package com.tripplannerai.dto.response.destination;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class DestinationCategoryDto {

    private String middleCategoryId;
    private String subCategoryId;
    private String middleCategoryName;
    private String subCategoryName;
    private String destinationId;
    private String destinationName;
    private String destinationAddr1;
    private String destinationAddr2;
    private String destinationTel;
    private String destinationContentId;
    private String destinationLatitude;
    private String destinationLongitude;
    private String destinationRating;
    private String destinationThumbnailImageUrl;

    public static DestinationCategoryDto of(DestinationCategoryQuery query){
        return DestinationCategoryDto.builder()
                .middleCategoryId(query.getMiddleCategoryId())
                .subCategoryId(query.getSubCategoryId())
                .middleCategoryName(query.getMiddleCategoryName())
                .subCategoryName(query.getSubCategoryName())
                .destinationId(query.getDestinationId())
                .destinationName(query.getDestinationName())
                .destinationAddr1(query.getDestinationAddr1())
                .destinationAddr2(query.getDestinationAddr2())
                .destinationTel(query.getDestinationTel())
                .destinationContentId(query.getDestinationContentId())
                .destinationLatitude(query.getDestinationLatitude())
                .destinationLongitude(query.getDestinationLongitude())
                .destinationRating(query.getDestinationRating())
                .destinationThumbnailImageUrl(query.getDestinationThumbnailImageUrl())
                .build();
    }
}
