package com.tripplannerai.dto.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryElement {
    private Long categoryId;
    private String categoryCode;
    private String name;
}
