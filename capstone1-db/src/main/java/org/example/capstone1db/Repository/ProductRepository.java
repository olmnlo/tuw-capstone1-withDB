package org.example.capstone1db.Repository;

import org.example.capstone1db.Model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}
