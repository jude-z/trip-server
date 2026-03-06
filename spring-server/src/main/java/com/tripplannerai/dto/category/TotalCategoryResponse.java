package com.tripplannerai.dto.category;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TotalCategoryResponse {
    private String code;
    private String message;
    private List<CategoryElement> content;

    public static TotalCategoryResponse of(String code, String message, List<CategoryElement> content) {
        return new TotalCategoryResponse(code, message, content);
    }
}
