package org.example.capstone1db.Service;

import lombok.RequiredArgsConstructor;
import org.example.capstone1db.Model.Category;
import org.example.capstone1db.Repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;


    public List<Category> getAllCategory(){
        return categoryRepository.findAll();
    }

    public Boolean addCategory(Category category){
        List<Category> categories = categoryRepository.findAll();
        for (Category c : categories) {
            if (c.getName().equals(category.getName())) {
                return false; //duplicated
            }
        }
        categoryRepository.save(category);
        return true;
    }

    public Boolean updateCategory(Integer id, Category category){
        if (!categoryRepository.existsById(id)) {
            return false;
        }

        Category oldCategory = categoryRepository.findById(id).orElse(null);
        if (oldCategory == null) {
            return false;
        }

        oldCategory.setName(category.getName());
        categoryRepository.save(oldCategory);
        return true;
    }

    public Boolean deleteCategory(Integer id){
        if (!categoryRepository.existsById(id)) {
            return false;
        }

        Category category = categoryRepository.findById(id).orElse(null);
        if (category == null) {
            return false;
        }

        categoryRepository.delete(category);
        return true;
    }

}
