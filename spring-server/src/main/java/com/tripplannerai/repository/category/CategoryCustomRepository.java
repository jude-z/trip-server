package com.tripplannerai.repository.category;

import com.tripplannerai.entity.category.Category;

import java.util.Optional;

public interface CategoryCustomRepository {
    Optional<Category> findCategoryByCodes(String cat1, String cat2, String cat3);
}
