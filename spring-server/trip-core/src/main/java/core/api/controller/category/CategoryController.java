package core.api.controller.category;

import core.api.response.ApiResponse;
import core.service.category.CategoryService;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<ApiResponse> fetchTotalCategory(@RequestParam(required = false) Long categoryId) {
        return ResponseEntity.ok(categoryService.fetchTotalCategory(categoryId));
    }
}
