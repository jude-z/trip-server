package com.tripplannerai.controller.category;

import com.tripplannerai.dto.category.TotalCategoryResponse;
import com.tripplannerai.service.category.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/total")
    public ResponseEntity<TotalCategoryResponse> fetchTotalCategory(@RequestParam(required = false) Long categoryId){
        TotalCategoryResponse totalCategoryResponse = categoryService.fetchTotalCategory(categoryId);

        return new ResponseEntity<>(totalCategoryResponse, HttpStatus.OK);
    }


}
