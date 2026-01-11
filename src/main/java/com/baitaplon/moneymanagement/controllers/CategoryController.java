package com.baitaplon.moneymanagement.controllers;

import com.baitaplon.moneymanagement.dto.CategoryDTO;
import com.baitaplon.moneymanagement.services.CategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryController {
    CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryDTO> saveCategory(@RequestBody CategoryDTO categoryDTO) {
        CategoryDTO savedCategory = categoryService.saveCategory(categoryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCategory);
    }

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        List<CategoryDTO> categories = categoryService.getCategoriesForCurrentProfile();
        return ResponseEntity.status(HttpStatus.OK).body(categories);
    }

    @GetMapping("/{type}")
    public ResponseEntity<List<CategoryDTO>> getCategoriesByType(@PathVariable String type) {
        List<CategoryDTO> categories = categoryService.getCategoriesByTypeForCurrentProfile(type);
        return ResponseEntity.status(HttpStatus.OK).body(categories);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable String id, @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO savedCategory = categoryService.updateCategory(id, categoryDTO);
        return ResponseEntity.status(HttpStatus.OK).body(savedCategory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable String id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.status(HttpStatus.OK).body("Xóa danh mục thành công");
    }
}
