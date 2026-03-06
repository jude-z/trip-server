package com.tripplannerai.service.category;

import com.tripplannerai.dto.category.TotalCategoryResponse;

public interface CategoryService {
    void saveCategoryFromApi() throws Exception;

    TotalCategoryResponse fetchTotalCategory(Long categoryId);
}
