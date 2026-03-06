package com.tripplannerai.repository.category;

import com.tripplannerai.dto.category.CategoryElement;
import com.tripplannerai.dto.category.TotalCategoryResponse;
import com.tripplannerai.entity.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category,Long>,CategoryCustomRepository {
    List<Category> category(Category category);
    @Query("select new com.tripplannerai.dto.category.CategoryElement" +
            "(c.categoryId,c.categoryCode,c.name) " +
            "from Category c where c.category.categoryId = :categoryId")
    List<CategoryElement> fetchTotalCategory(Long categoryId);
    @Query("select new com.tripplannerai.dto.category.CategoryElement" +
            "(c.categoryId,c.categoryCode,c.name) " +
            "from Category c where c.category.categoryId is null")
    List<CategoryElement> fetchTotalCategory();
}
