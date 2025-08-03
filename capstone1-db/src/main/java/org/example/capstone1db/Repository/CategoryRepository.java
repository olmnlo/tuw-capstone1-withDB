package org.example.capstone1db.Repository;

import org.example.capstone1db.Model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
