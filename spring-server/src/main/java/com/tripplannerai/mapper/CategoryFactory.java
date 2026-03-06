package com.tripplannerai.mapper;

import com.tripplannerai.entity.category.Category;

public class CategoryFactory {
    public static Category of(String name,String code, Category category) {
        return Category.builder()
                .name(name)
                .categoryCode(code)
                .category(category)
                .build();
    }

    public static Category of(String name,String code) {
        return Category.builder()
                .name(name)
                .categoryCode(code)
                .build();
    }
}
