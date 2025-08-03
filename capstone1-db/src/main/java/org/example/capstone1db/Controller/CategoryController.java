package org.example.capstone1db.Controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.capstone1db.Api.ApiResponse;
import org.example.capstone1db.Model.Category;
import org.example.capstone1db.Service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/category")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<?> getCategories(){
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.getAllCategory());
    }

    @PostMapping
    public ResponseEntity<?> addCategory(@Valid @RequestBody Category category, Errors error){
        if (error.hasErrors()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error.getFieldError().getDefaultMessage());
        }
        if(categoryService.addCategory(category)) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("category add successfully"));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("category is duplicated"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Integer id, @Valid @RequestBody Category category, Errors error){
        if (error.hasErrors()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error.getFieldError().getDefaultMessage());
        }

        if(categoryService.updateCategory(id, category)){
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("category updated successfully"));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("category not found"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Integer id){
        if(categoryService.deleteCategory(id)){
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("category deleted successfully"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("category not found"));
    }

}
